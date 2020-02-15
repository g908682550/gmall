package com.npu.gmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}
