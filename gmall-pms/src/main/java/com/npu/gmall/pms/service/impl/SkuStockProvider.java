package com.npu.gmall.pms.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SkuStockProvider {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendOrderFinishInfo(String orderSn){

    }

}
