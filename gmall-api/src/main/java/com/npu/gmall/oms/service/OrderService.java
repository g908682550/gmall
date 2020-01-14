package com.npu.gmall.oms.service;

import com.npu.gmall.oms.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.npu.gmall.vo.order.OrderConfirmVo;
import com.npu.gmall.vo.order.OrderCreateVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author gy
 * @since 2020-01-02
 */
public interface OrderService extends IService<Order> {

    //订单确认
    OrderConfirmVo orderConfirm(Long id);

    OrderCreateVo createOrder(BigDecimal totalPrice, Long addressId,String notes);

    String pay(String orderSn, String accessToken);

    /**
     * 检查最终的支付结果
     * @param params
     * @return
     */
    String resolvePayResult(Map<String, String> params);
}
