package com.npu.gmall.demo.controller;

import com.npu.gmall.demo.service.RedisIncrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    RedisIncrService redisIncrService;

    @GetMapping("/incr")
    public String incr(){
        redisIncrService.incr();
        return "ok";
    }

    @GetMapping("/incr2")
    public String incr2(){
        redisIncrService.incrDistribute();
        return "ok";
    }

}
