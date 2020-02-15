package com.npu.gmallpayment.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.npu.gmall.oms.entity.Order;
import com.npu.gmall.oms.entity.OrderItem;
import com.npu.gmall.oms.service.OrderService;
import com.npu.gmall.payment.service.PaymentService;
import com.npu.gmallpayment.config.AlipayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Service
@Component
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Reference
    OrderService orderService;

    /**
     * 订单支付接口
     * @param orderSn
     * @param accessToken
     * @return
     */
    @Override
    public String pay(String orderSn, String accessToken) {

        Order order = orderService.selectOne(orderSn);

        List<OrderItem> orderItems = orderService.selectList(orderSn);

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

    /**
     * 参数校验
     * @param params
     * @return
     */
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
            orderService.paySuccess(out_trade_no);
            log.debug("订单【{}】,已经支付成功...可以退款。数据库都改了",out_trade_no);
        }
        return "success";
    }
}
