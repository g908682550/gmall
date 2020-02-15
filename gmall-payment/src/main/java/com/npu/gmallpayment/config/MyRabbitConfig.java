package com.npu.gmallpayment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MyRabbitConfig {

    @Bean
    public Queue createPayEndQueue(){
        log.debug("队列创建完成");
        return new Queue("payEnd",true,false,false);
    }

    @Bean
    public Exchange createOrderExchange(){
        log.debug("交换机创建完成");
        return new DirectExchange("payExchange",true,false,null);
    }

    @Bean
    public Binding createPayBinding(){
        log.debug("绑定关系创建完成");
        return new Binding("payEnd",Binding.DestinationType.QUEUE,"payExchange","payEnd",null);
    }
}
