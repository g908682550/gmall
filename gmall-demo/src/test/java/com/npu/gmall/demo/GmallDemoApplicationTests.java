package com.npu.gmall.demo;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;

@SpringBootTest
class GmallDemoApplicationTests {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    void contextLoads() {
        System.out.println(jedisPool);
        Jedis jedis = jedisPool.getResource();
        jedis.set("hello","666");
        System.out.println(jedis.get("hello"));
    }

    @Test
    public void fun1(){
        User user = new User("guoyi", 111);

        rabbitTemplate.convertAndSend("direct_exchange","hello.world",user);

        System.out.println("消息发送完成");
    }

    @Test
    public void fun2(){
        //创建队列
        Queue queue = new Queue("my-queue-01",true,false,false);
        amqpAdmin.declareQueue(queue);
    }

    @Test
    public void createExchange(){
        //创建fanout交换机
        Exchange exchange=new FanoutExchange("fanout_change",true,false);
        amqpAdmin.declareExchange(exchange);
    }

    @Test
    public void createBinding(){
        //创建绑定关系
        //	public Binding(String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments)
        Binding binding = new Binding("my-queue-01",
                Binding.DestinationType.QUEUE,
                "fanout_change",
                "hello",
                null);
        amqpAdmin.declareBinding(binding);
    }

}

class User implements Serializable {
    String username;
    Integer id;

    public User() {
    }

    public User(String username, Integer id) {
        this.username = username;
        this.id=id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
