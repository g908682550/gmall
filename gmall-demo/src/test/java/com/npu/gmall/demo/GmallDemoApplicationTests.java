package com.npu.gmall.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SpringBootTest
class GmallDemoApplicationTests {

    @Autowired
    JedisPool jedisPool;
    @Test
    void contextLoads() {
        System.out.println(jedisPool);
        Jedis jedis = jedisPool.getResource();
        jedis.set("hello","666");
        System.out.println(jedis.get("hello"));
    }

}
