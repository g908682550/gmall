package com.npu.gmall.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.npu.gmall.constant.OrderStatusEnum;
import com.npu.gmall.oms.entity.Order;
import com.npu.gmall.oms.mapper.OrderMapper;
import com.npu.gmall.vo.payment.PaymentInfo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderConsumer {

    @Autowired
    OrderMapper orderMapper;

    /**
     * 手动确认
     * @param paymentInfo
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {"payEnd"})
    public void receiveOrderMessage(PaymentInfo paymentInfo, Message message, Channel channel) throws IOException {
        try{
            log.debug("监听到订单状态"+paymentInfo);
            String orderSn=paymentInfo.getOrderId();
            String status=paymentInfo.getStatus();
            Order order = new Order();
//            if(status.equals("TRADE_SUCCESS"))
              order.setStatus(OrderStatusEnum.PAYED.getCode());
//            else if(status.equals("TRADE_FINISHED")) order.setStatus(OrderStatusEnum.FINISHED.getCode());
            orderMapper.update(order,new UpdateWrapper<Order>().eq("order_sn",orderSn));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }
    }

}
