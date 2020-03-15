package com.npu.gmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue createPayEndQueue(){
        return new Queue("payEnd",true,false,false);
    }

    @Bean
    public Exchange createOrderExchange(){
        return new DirectExchange("payExchange",true,false,null);
    }

    @Bean
    public Binding createPayBinding(){
        return new Binding("payEnd",Binding.DestinationType.QUEUE,"payExchange","payEnd",null);
    }

    //死信交换机
    @Bean
    public Exchange createDelayExchange(){
        return new DirectExchange("user.order.delay.exchange",true,false,null);
    }

    //死信队列
    @Bean
    public Queue createDelayQueue(){

        Map<String,Object> arguments=new HashMap<>();

        arguments.put("x-message-ttl",30*1000);
        arguments.put("x-dead-letter-exchange","user.orderTTL.exchange");//消息死了去哪个交换机
        arguments.put("x-dead-letter-routing-key","orderTTL");//死信发出去的路由键

        return new Queue("user.order.delay.queue",true,false,false);
    }

    @Bean
    public Binding delayBinding(){
        return new Binding("user.order.delay.queue", Binding.DestinationType.QUEUE,"user.order.delay.exchange","order_delay",null);
    }

    @Bean
    public Exchange createTTLExchange(){
        return new DirectExchange("user.orderTTL.exchange",true,false,null);
    }

    /**
     * 所有订单超过30分钟后都会来到该队列，监听该队列对订单进行判断
     * @return
     */
    @Bean
    public Queue createTTLQueue(){
        return new Queue("user.orderTTL.queue",true,false,false);
    }

    @Bean
    public Binding deadBinding(){
        return new Binding("user.orderTTL.queue", Binding.DestinationType.QUEUE,"user.orderTTL.exchange","orderTTL",null);
    }



}
