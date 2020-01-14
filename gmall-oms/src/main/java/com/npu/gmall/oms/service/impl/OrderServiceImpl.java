package com.npu.gmall.oms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.npu.gmall.cart.service.CartService;
import com.npu.gmall.cart.vo.CartItem;
import com.npu.gmall.constant.OrderStatusEnume;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.oms.component.MemberComponent;
import com.npu.gmall.oms.config.AlipayConfig;
import com.npu.gmall.oms.entity.Order;
import com.npu.gmall.oms.entity.OrderItem;
import com.npu.gmall.oms.mapper.OrderItemMapper;
import com.npu.gmall.oms.mapper.OrderMapper;
import com.npu.gmall.oms.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npu.gmall.pms.entity.SkuStock;
import com.npu.gmall.pms.service.ProductService;
import com.npu.gmall.pms.service.SkuStockService;
import com.npu.gmall.to.es.EsProduct;
import com.npu.gmall.to.es.EsProductAttributeValue;
import com.npu.gmall.to.es.EsSkuProductInfo;
import com.npu.gmall.ums.entity.Member;
import com.npu.gmall.ums.entity.MemberReceiveAddress;
import com.npu.gmall.ums.service.MemberService;
import com.npu.gmall.vo.order.OrderConfirmVo;
import com.npu.gmall.vo.order.OrderCreateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2020-01-14
 */
@Slf4j
@Service
@Component
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Reference
    MemberService memberService;

    @Reference
    CartService cartService;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberComponent memberComponent;

    @Autowired
    OrderMapper orderMapper;

    @Reference
    SkuStockService skuStockService;

    @Reference
    ProductService productService;
    ThreadLocal<List<CartItem>> threadLocal=new ThreadLocal<>();

    @Override
    public String resolvePayResult(Map<String, String> params) {
        boolean signVerified = true;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
                    AlipayConfig.sign_type);
            System.out.println("验签：" + signVerified);

        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
        }
        // 商户订单号
        String out_trade_no =params.get("out_trade_no");
        // 支付宝流水号
        String trade_no =params.get("trade_no");
        // 交易状态
        String trade_status =params.get("trade_status");
        if (trade_status.equals("TRADE_FINISHED")) {
            //改订单状态
            log.debug("订单【{}】,已经完成...不能再退款。数据库都改了",out_trade_no);
        } else if (trade_status.equals("TRADE_SUCCESS")) {
            Order order = new Order();
            order.setStatus(OrderStatusEnume.PAYED.getCode());
            orderMapper.update(order,new UpdateWrapper<Order>().eq("order_sn",out_trade_no));
            log.debug("订单【{}】,已经支付成功...可以退款。数据库都改了",out_trade_no);
        }
        return "success";
    }

    @Override
    @Transactional
    public OrderCreateVo createOrder(BigDecimal frontTotalPrice, Long addressId,String notes) {

        //防重复
        String orderToken = RpcContext.getContext().getAttachment("orderToken");
        //验证令牌的第一种失败
        if(StringUtils.isEmpty(orderToken)){
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("操作出现错误，请重新尝试");
            return orderCreateVo;
        }

        if(!orderToken.contains("_")){
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("操作出现错误，请重新尝试");
            return orderCreateVo;
        }

        String[] split = orderToken.split("_");
        if(split.length!=3){
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("操作出现错误，请重新尝试");
            return orderCreateVo;
        }
        Long createTime= Long.parseLong(split[1]);
        Long timeOut=Long.parseLong(split[2]);
        if(System.currentTimeMillis()-createTime-timeOut>=0){
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("页面超时，请重新刷新");
            return orderCreateVo;
        }

        //验证重复
        Long remove = stringRedisTemplate.opsForSet().remove(SysCacheConstant.ORDER_UNIQUE_TOKEN, orderToken);
        if(remove==0){
            //说明令牌非法
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("创建失败，刷新重试");
            return orderCreateVo;
        }

        //隐时传参禁止传以下参数,token,timeout,retries,....dubbo标签规定的所有属性都是关键字，不能隐式传参

        //获取到当前会员信息
        String accessToken = RpcContext.getContext().getAttachment("accessToken");

        Member member = memberComponent.getMemberByAccessToken(accessToken);

        //验价
        Boolean vaildPrice = vaildPrice(frontTotalPrice, accessToken, addressId);
        if(!vaildPrice){
            OrderCreateVo orderCreateVo=new OrderCreateVo();
            orderCreateVo.setLimit(false);//比价失败
            orderCreateVo.setToken("比价失败");
            return orderCreateVo;
        }
        OrderCreateVo orderCreateVo = initOrderCreateVo(frontTotalPrice, addressId, accessToken, member);

        //加工处理数据
        //初始化数据库订单信息
        Order order = initOrderInfo(frontTotalPrice, addressId, notes, member, orderCreateVo.getOrderSn());
        //保存订单，数据库幂等，幂等字段需要唯一索引
        orderMapper.insert(order);
        //构造/保存订单项信息
        saveOrderItem(order,accessToken);

        //3、清除购物车中已经下单的商品

        return orderCreateVo;
    }

    private void saveOrderItem(Order order,String accessToken) {
        List<CartItem> cartItems = threadLocal.get();
        List<OrderItem> orderItems=new ArrayList<>();
        List<Long> skuIds=new ArrayList<>();
        cartItems.forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();

            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
            Long skuId = cartItem.getSkuId();
            EsProduct esProduct = productService.productSkuInfo(skuId);
            SkuStock skuStock=new SkuStock();
            List<EsSkuProductInfo> skuProductInfos = esProduct.getSkuProductInfos();
            String valuesJson=null;
            for(EsSkuProductInfo skuProductInfo:skuProductInfos){
                if(skuId==skuProductInfo.getId()){
                    List<EsProductAttributeValue> values = skuProductInfo.getAttributeValues();
                    valuesJson = JSON.toJSONString(values);
                    BeanUtils.copyProperties(skuProductInfo,skuStock);
                }
            }
            orderItem.setProductId(esProduct.getId());
            orderItem.setProductPic(esProduct.getPic());
            orderItem.setProductName(esProduct.getName());
            orderItem.setProductBrand(esProduct.getBrandName());
            orderItem.setProductSn(esProduct.getProductSn());
            //当前购物项的价格
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setProductQuantity(cartItem.getCount());
            orderItem.setProductSkuId(skuId);
            orderItem.setProductSkuCode(skuStock.getSkuCode());
            orderItem.setProductCategoryId(esProduct.getProductCategoryId());
            orderItem.setSp1(skuStock.getSp1());
            orderItem.setSp2(skuStock.getSp2());
            orderItem.setSp3(skuStock.getSp3());
            orderItem.setProductAttr(valuesJson);

            orderItems.add(orderItem);
            orderItemMapper.insert(orderItem);
            skuIds.add(orderItem.getProductSkuId());
        });
        //清除购物车中已经下单的商品
        cartService.removeCartItem(accessToken,skuIds);
    }

    /**
     * 构造订单创建vo
     * @param frontTotalPrice
     * @param addressId
     * @param accessToken
     * @param member
     * @return
     */
    private OrderCreateVo initOrderCreateVo(BigDecimal frontTotalPrice, Long addressId, String accessToken, Member member) {
        OrderCreateVo orderCreateVo=new OrderCreateVo();
        //设置订单号
        String orderSn = IdWorker.getTimeId();
        orderCreateVo.setOrderSn(orderSn);
        //设置收货地址
        orderCreateVo.setAddressId(addressId);
        //设置购物车中的数据
        List<CartItem> cartItems = cartService.getCartItemForOrder(accessToken);
        orderCreateVo.setCartItems(cartItems);
        //设置会员id
        orderCreateVo.setMemberId(member.getId());
        //总价格
        orderCreateVo.setTotalPrice(frontTotalPrice);
        //描述信息
        orderCreateVo.setDetailInfo(cartItems.get(0).getName());
        return orderCreateVo;
    }

    /**
     * 初始化数据库订单信息
     * @param frontTotalPrice
     * @param addressId
     * @param notes
     * @param member
     * @param orderSn
     * @return
     */
    private Order initOrderInfo(BigDecimal frontTotalPrice, Long addressId, String notes, Member member, String orderSn) {
        Order order=new Order();
        order.setMemberId(member.getId());
        order.setOrderSn(orderSn);
        order.setCreateTime(new Date());
        order.setAutoConfirmDay(7);
        //order.setBillContent();
        order.setNote(notes);
        order.setMemberUsername(member.getUsername());
        order.setTotalAmount(frontTotalPrice);
        order.setFreightAmount(new BigDecimal("10"));
        //订单状态
        order.setStatus(OrderStatusEnume.UNPAY.getCode());
        //设置收货人信息
        MemberReceiveAddress address=memberService.getMemberAddressByAddressId(addressId);
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverCity(address.getCity());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverDetailAddress(address.getDetailAddress());
        return order;
    }

    private Boolean vaildPrice(BigDecimal frontPrice,String accessToken,Long addressId){
        //1、拿到购物车
        List<CartItem> cartItems = cartService.getCartItemForOrder(accessToken);
        threadLocal.set(cartItems);
        BigDecimal bigDecimal = new BigDecimal("0");
        //我们的总价格是实时查出来的，必须去库存服务查出最新的价格
        for(CartItem cartItem:cartItems){
            //bigDecimal= bigDecimal.add(cartItem.getTotalPrice());
            Long skuId = cartItem.getSkuId();
            BigDecimal newPrice=skuStockService.getSkuPriceBySkuId(skuId);
            cartItem.setPrice(newPrice);
            Integer count = cartItem.getCount();
            BigDecimal totalPrice = newPrice.multiply(new BigDecimal(count.toString()));//当前项的总价
            bigDecimal= bigDecimal.add(totalPrice);
        }
        //获取运费
        BigDecimal transPrice = new BigDecimal("10");

        BigDecimal totalPrice = bigDecimal.add(transPrice);

        return totalPrice.compareTo(frontPrice)==0?true:false;
    }

    @Override
    public OrderConfirmVo orderConfirm(Long id) {

        //获取上一步隐式传参带来的accessToken
        String accessToken = RpcContext.getContext().getAttachment("accessToken");

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        //设置收货地址
        confirmVo.setAddresses(memberService.getMemberAddress(id));

        //设置优惠券信息
        confirmVo.setCoupons(null);

        //设置购物项信息
        List<CartItem> cartItems=cartService.getCartItemForOrder(accessToken);
        confirmVo.setCartItems(cartItems);
        //设置订单的防重令牌
        String orderToken = UUID.randomUUID().toString().replace("-", "");

        //可以给令牌加上业务时间
        orderToken=orderToken+"_"+System.currentTimeMillis()+"_"+10*60*1000;

        confirmVo.setOrderToken(orderToken);
        //保存防重令牌
        stringRedisTemplate.opsForSet().add(SysCacheConstant.ORDER_UNIQUE_TOKEN,orderToken);

        //运费是远程计算出来的
        confirmVo.setTransPrice(new BigDecimal("10"));

        //计算价格,数量等
        confirmVo.setCouponPrice(null);
        cartItems.forEach(cartItem -> {
            confirmVo.setCount(confirmVo.getCount()+cartItem.getCount());
            confirmVo.setProductTotalPrice(confirmVo.getProductTotalPrice().add(cartItem.getTotalPrice()));
        });

        confirmVo.setTotalPrice(confirmVo.getProductTotalPrice().add(confirmVo.getTransPrice()));
        return confirmVo;
    }

    @Override
    public String pay(String orderSn, String accessToken) {

        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_sn", orderSn));

        List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_sn", orderSn));

        String productName = orderItems.get(0).getProductName();
        StringBuffer allName=new StringBuffer();
        for(OrderItem orderItem:orderItems){
            allName.append(orderItem.getProductName()).append("<br/>");
        }
        return payOrder(orderSn,order.getTotalAmount().toString(),"npu商城-"+productName,allName.toString());
    }

    /**
     * 支付方法
     * @param out_trade_no 商户的订单号
     * @param total_amount  总金额
     * @param subject       标题
     * @param body          描述
     * @return
     */
    private String payOrder(String out_trade_no,
                            String total_amount,
                            String subject,
                            String body) {
        // 1、创建支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id,
                AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key,
                AlipayConfig.sign_type);

        // 2、创建一次支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        // 商户订单号，商户网站订单系统中唯一订单号，必填
        // 付款金额，必填
        // 订单名称，必填
        // 商品描述，可空

        // 3、构造支付请求数据
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"total_amount\":\"" + total_amount
                + "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = "";
        try {
            // 4、请求
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;// 支付跳转页的代码

    }

}
