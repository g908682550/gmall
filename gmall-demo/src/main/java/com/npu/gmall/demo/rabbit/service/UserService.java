package com.npu.gmall.demo.rabbit.service;

import com.npu.gmall.demo.rabbit.bean.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 消息确认机制
 *  1）如果这个消息收到了，在处理期间出现了运行时异常，默认认为消息没有被正确处理，消息状态是unack状态
 *      unack的消息消息队列会再次尝试把整个消息发送给消费者
 *  2）不要让消息队列自己判断ack和unack，手动确认ack机制。可以防止消息重复入队
 *  解决：
 *      1）手动ack
 *      2）接口幂等性，在本地维护一个日志表，记录哪些会员哪些商品哪个订单已经减过库存了。再来同样的消息就不减了
 *
 *  手动ack
 *      1、开启手动ack spring.rabbitmq.listener.simple.acknowledge-mode=manual
 *      2、public void listener(){
 *          try{
 *              //处理消息,回复
 *              channel.basicAck("");
 *          }
 *      }
 */
@Service
public class UserService {

//    /**
//     * 监听的方法上可以写以下参数
//     * 1、Message，既能获取消息的内容字节，还能获取到消息的其它属性
//     * 2、User user，明确消息内容是该对象，可直接用该对象进行接受
//     * 3、com.rabbitmq.client.Channel:通道
//     * @param message
//     * @param user
//     */
//    @RabbitListener(queues = {"hello"})
//    public void receiveUserMessage(Message message, User user, Channel channel) throws IOException {
//        System.out.println(message.getClass());
//        System.out.println(user);
//
//        //通过channel可以把消息拒绝,让rabbitmq再发给别人
//        channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
//    }

    @RabbitListener(queues = {"order_queue"})
    public void receiveOrderMessage(Order order, Message message, Channel channel) throws  Exception{
        System.out.println("监听到新的订单生成"+order);
        Long skuId = order.getSkuId();
        Integer num = order.getNum();
        System.out.println("库存系统正在扣除");
        if(num%2==0){
            System.out.println("库存系统扣除失败");
            //回复消息处理失败并且重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
            throw new RuntimeException("库存扣除失败");
        }
        System.out.println("扣除成功");
        //只回复本条消息
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitListener(queues = {"user.order.queue"})
    public void closeOrder(Order order,Channel channel,Message message) throws Exception{
        System.out.println("收到过期订单："+order+"正在关闭订单");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
