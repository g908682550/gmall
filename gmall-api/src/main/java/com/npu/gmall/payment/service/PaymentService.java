package com.npu.gmall.payment.service;

import java.util.Map;

public interface PaymentService {

    String pay(String orderSn, String accessToken);

    /**
     * 检查最终的支付结果
     * @param params
     * @return
     */
    String resolvePayResult(Map<String, String> params);
}
