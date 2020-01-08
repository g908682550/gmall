package com.npu.gmall.demo.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class AppJedisConfig {

    @Bean
    public JedisPool jedisPoolConfig(RedisProperties properties) throws Exception{
        JedisPoolConfig config=new JedisPoolConfig();
        RedisProperties.Pool pool = properties.getJedis().getPool();
        config.setMaxIdle(pool.getMaxIdle());
        config.setMaxTotal(pool.getMaxActive());
        JedisPool jedisPool = new JedisPool(config, properties.getHost(), properties.getPort());
        return jedisPool;
    }
}
