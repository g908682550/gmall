package com.npu.gmall.oms.service.impl;

import com.npu.gmall.oms.entity.OrderItem;
import com.npu.gmall.oms.service.OrderService;
import com.npu.gmall.vo.product.SkuStockInfo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderProvider {

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 订单支付成功后，发送消息通知减库存
     * @param orderSn
     */
    public void sendSkuStockMessage(String orderSn){
        /**
         * 获取所有订单项
         */
        List<OrderItem> orderItems=orderService.selectList(orderSn);
        List<SkuStockInfo> skuStockInfos=new ArrayList<>();
        orderItems.forEach(orderItem->{
            skuStockInfos.add(new SkuStockInfo(orderItem.getProductSkuId(),orderItem.getProductQuantity()));
        });
        rabbitTemplate.convertAndSend("skuStockExchange","deleteSkuStock",skuStockInfos);
    }

}
