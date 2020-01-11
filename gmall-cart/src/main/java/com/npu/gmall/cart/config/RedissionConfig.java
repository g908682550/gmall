package com.npu.gmall.cart.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissionConfig {

    @Bean
    RedissonClient redission() throws Exception{
        Config config=new Config();
        config.useSingleServer().setAddress("redis://192.168.220.128:6379");
        return Redisson.create(config);
    }

}
