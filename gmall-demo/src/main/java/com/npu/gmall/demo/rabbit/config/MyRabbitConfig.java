package com.npu.gmall.demo.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRabbitConfig {

    /**
     * 可以直接在容器中放入加入队列、交换机、绑定关系等，如果已经存在则不会创建
     * @return
     */
    @Bean
    public Queue createOrderQueue(){
        return new Queue("order_queue",true,false,false);
    }

    @Bean
    public Exchange createOrderExchange(){
        return new DirectExchange("order_change",true,false,null);
    }

    @Bean
    public Binding createOrderBinding(){
        return new Binding("order_queue", Binding.DestinationType.QUEUE,"order_change","createOrder",null);
    }

}
