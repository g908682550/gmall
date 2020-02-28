package com.npu.gmall.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.npu.gmall.constant.OrderStatusEnum;
import com.npu.gmall.oms.entity.Order;
import com.npu.gmall.oms.service.OrderService;
import com.npu.gmall.pms.service.SkuStockService;
import com.npu.gmall.vo.payment.PaymentInfo;
import com.rabbitmq.client.Channel;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.tools.shell.IO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderConsumer {

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderProvider orderProvider;

    /**
     * 收到支付成功的消息，手动确认，更改订单状态
     * @param paymentInfo
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {"payEnd"})
    public void receivePayEndMessage(PaymentInfo paymentInfo, Message message, Channel channel) throws IOException {
        try{
            log.debug("监听到订单状态"+paymentInfo);
            String orderSn=paymentInfo.getOrderId();
            String status=paymentInfo.getStatus();
            Order order = new Order();
            order.setStatus(OrderStatusEnum.PAYED.getCode());
            //更改订单状态为已支付
            orderService.update(order,new UpdateWrapper<Order>().eq("order_sn",orderSn));
            //手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            //发送一条消息给库存系统，通知减库存
            orderProvider.sendSkuStockMessage(orderSn);
        }catch (Exception e){
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

    /**
     * 收到库存扣除成功的消息后，更改订单的数据库状态
     * @param orderSn
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {"skuStockFinish"})
    public void receiveSkuStockMessage(String orderSn,Message message,Channel channel) throws IOException {
        try{
            Order order=new Order();
            order.setStatus(OrderStatusEnum.FINISHED.getCode());
            orderService.update(order,new UpdateWrapper<Order>().eq("order_sn",orderSn));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

    /**
     * 所有到达该队列的消息均为超过30分钟的订单，对订单进行判断，若仍为支付，则关闭，如已支付，什么都不做
     * @param orderSn
     */
    @RabbitListener(queues = {"user.orderTTL.queue"})
    public void receiveTTLOrder(String orderSn,Message message,Channel channel) throws IOException {
        try{
            Order order = orderService.selectOne(orderSn);
            if(order.getStatus()==OrderStatusEnum.UNPAY.getCode()){
                order.setStatus(OrderStatusEnum.UNVAILED.getCode());
                orderService.update(order,new UpdateWrapper<Order>().eq("order_sn",orderSn));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
        }catch (Exception e){
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

}
