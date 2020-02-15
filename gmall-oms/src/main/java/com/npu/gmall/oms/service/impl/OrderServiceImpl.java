package com.npu.gmall.oms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.npu.gmall.oms.service.CartService;
import com.npu.gmall.oms.service.OrderItemService;
import com.npu.gmall.vo.cart.CartItem;
import com.npu.gmall.constant.OrderStatusEnum;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.oms.component.MemberComponent;
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

    @Reference
    ProductService productService;

    @Reference
    SkuStockService skuStockService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberComponent memberComponent;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    ThreadLocal<List<CartItem>> threadLocal = new ThreadLocal<>();

    /**
     * 订单确认
     *
     * @param id
     * @return
     */
    @Override
    public OrderConfirmVo orderConfirm(Long id) {

        //获取上一步隐式传参带来的accessToken
        String accessToken = RpcContext.getContext().getAttachment("accessToken");

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        //设置收货地址
        confirmVo.setAddresses(memberService.getMemberAddress(id));
//
//        //设置优惠券信息
//        confirmVo.setCoupons(null);

        //设置购物项信息
        List<CartItem> cartItems = cartService.getCartItemForOrder(accessToken);
        confirmVo.setCartItems(cartItems);
        //设置订单的防重令牌
        String orderToken = UUID.randomUUID().toString().replace("-", "");

        //可以给令牌加上业务时间
        orderToken = orderToken + "_" + System.currentTimeMillis() + "_" + 10 * 60 * 1000;

        confirmVo.setOrderToken(orderToken);
        //保存防重令牌
        stringRedisTemplate.opsForSet().add(SysCacheConstant.ORDER_UNIQUE_TOKEN, orderToken);

        //运费是远程计算出来的
        confirmVo.setTransPrice(new BigDecimal("10"));

        //计算价格,数量等
        confirmVo.setCouponPrice(null);
        cartItems.forEach(cartItem -> {
            confirmVo.setCount(confirmVo.getCount() + cartItem.getCount());
            confirmVo.setProductTotalPrice(confirmVo.getProductTotalPrice().add(cartItem.getTotalPrice()));
        });

        confirmVo.setTotalPrice(confirmVo.getProductTotalPrice().add(confirmVo.getTransPrice()));
        return confirmVo;
    }

    /**
     * 订单创建
     *
     * @param frontTotalPrice
     * @param addressId
     * @param notes
     * @return
     */
    @Override
    @Transactional
    public OrderCreateVo createOrder(BigDecimal frontTotalPrice, Long addressId, String notes) {


        String orderToken = RpcContext.getContext().getAttachment("orderToken");
        //防重复，接口幂等性判断
        if (!validIde(orderToken)) {
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("订单创建失败，请重新尝试");
            return orderCreateVo;
        }

        //获取到当前用户信息
        String accessToken = RpcContext.getContext().getAttachment("accessToken");

        Member member = memberComponent.getMemberByAccessToken(accessToken);

        //验价，前端传过来的价格和自己去数据库查到的价格是否相同
        Boolean vaildPrice = validPrice(frontTotalPrice, accessToken, addressId);
        if (!vaildPrice) {
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setLimit(false);//比价失败
            orderCreateVo.setToken("比价失败");
            return orderCreateVo;
        }

        //根据订单信息创建订单Vo
        OrderCreateVo orderCreateVo = initOrderCreateVo(frontTotalPrice, addressId, accessToken, member);
        //初始化数据库订单信息
        Order order = initOrderInfo(frontTotalPrice, addressId, notes, member, orderCreateVo.getOrderSn());
        //保存订单，数据库幂等，幂等字段需要唯一索引
        orderMapper.insert(order);
        //构造/保存订单项信息
        saveOrderItem(order, accessToken);
        return orderCreateVo;
    }

    /**
     * 根据订单号获取订单
     *
     * @param order_sn
     * @return
     */
    @Override
    public Order selectOne(String order_sn) {
        return orderMapper.selectOne(new QueryWrapper<Order>().eq("order_sn", order_sn));
    }

    /**
     * 根据订单号获取订单项
     *
     * @param orderSn
     * @return
     */
    @Override
    public List<OrderItem> selectList(String orderSn) {
        return orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_sn", orderSn));
    }

    /**
     * 保存订单的订单项到数据库
     *
     * @param order
     * @param accessToken
     */
    private void saveOrderItem(Order order, String accessToken) {
        //验价过程已经获取过购物项的数据，此时直接从ThreadLocal中获取
        List<CartItem> cartItems = threadLocal.get();
        List<OrderItem> orderItems = new ArrayList<>();
        List<Long> skuIds = new ArrayList<>();
        cartItems.forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();

            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
            Long skuId = cartItem.getSkuId();
            EsProduct esProduct = productService.productSkuInfo(skuId);
            SkuStock skuStock = new SkuStock();
            List<EsSkuProductInfo> skuProductInfos = esProduct.getSkuProductInfos();
            String valuesJson = null;
            for (EsSkuProductInfo skuProductInfo : skuProductInfos) {
                if (skuId == skuProductInfo.getId()) {
                    List<EsProductAttributeValue> values = skuProductInfo.getAttributeValues();
                    valuesJson = JSON.toJSONString(values);
                    BeanUtils.copyProperties(skuProductInfo, skuStock);
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
    }

//    /**
//     * 支付成功，修改数据库订单的状态
//     * @param out_trade_no
//     */
//    @Override
//    public void paySuccess(String out_trade_no) {
//        Order order = new Order();
//        order.setStatus(OrderStatusEnum.PAYED.getCode());
//        orderMapper.update(order,new UpdateWrapper<Order>().eq("order_sn",out_trade_no));
//    }

    /**
     * 订单创建的vo
     *
     * @param frontTotalPrice
     * @param addressId
     * @param accessToken
     * @param member
     * @return
     */
    private OrderCreateVo initOrderCreateVo(BigDecimal frontTotalPrice, Long addressId, String accessToken, Member member) {
        OrderCreateVo orderCreateVo = new OrderCreateVo();
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
     * 初始化要保存到数据库的订单的对象
     *
     * @param frontTotalPrice
     * @param addressId
     * @param notes
     * @param member
     * @param orderSn
     * @return
     */
    private Order initOrderInfo(BigDecimal frontTotalPrice, Long addressId, String notes, Member member, String orderSn) {
        Order order = new Order();
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
        order.setStatus(OrderStatusEnum.UNPAY.getCode());
        //设置收货人信息
        MemberReceiveAddress address = memberService.getMemberAddressByAddressId(addressId);
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverCity(address.getCity());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverDetailAddress(address.getDetailAddress());
        return order;
    }

    /**
     * 接口的幂等性验证
     *
     * @param orderToken
     * @return
     */
    private boolean validIde(String orderToken) {
        //验证令牌的第一种失败
        if (StringUtils.isEmpty(orderToken)) return false;


        if (!orderToken.contains("_")) return false;


        String[] split = orderToken.split("_");
        if (split.length != 3) return false;

        Long createTime = Long.parseLong(split[1]);
        Long timeOut = Long.parseLong(split[2]);
        if (System.currentTimeMillis() - createTime - timeOut >= 0) return false;

        //验证重复
        Long remove = stringRedisTemplate.opsForSet().remove(SysCacheConstant.ORDER_UNIQUE_TOKEN, orderToken);
        if (remove == 0) return false;
        return true;
    }

    /**
     * 提交订单时验证和数据库的价格
     *
     * @param frontPrice
     * @param accessToken
     * @param addressId
     * @return
     */
    private Boolean validPrice(BigDecimal frontPrice, String accessToken, Long addressId) {
        //1、拿到购物车的购物项
        List<CartItem> cartItems = cartService.getCartItemForOrder(accessToken);
        //将购物项保存在threadLocal中，本方法内同一个线程都可以用
        threadLocal.set(cartItems);
        BigDecimal bigDecimal = new BigDecimal("0");
        //我们的总价格是实时查出来的，必须去库存服务查出最新的价格
        for (CartItem cartItem : cartItems) {
            //bigDecimal= bigDecimal.add(cartItem.getTotalPrice());
            Long skuId = cartItem.getSkuId();
            BigDecimal newPrice = skuStockService.getSkuPriceBySkuId(skuId);
            cartItem.setPrice(newPrice);
            Integer count = cartItem.getCount();
            BigDecimal totalPrice = newPrice.multiply(new BigDecimal(count.toString()));//当前项的总价
            bigDecimal = bigDecimal.add(totalPrice);
        }
        //获取运费
        BigDecimal transPrice = new BigDecimal("10");

        BigDecimal totalPrice = bigDecimal.add(transPrice);

        return totalPrice.compareTo(frontPrice) == 0 ? true : false;
    }

}
