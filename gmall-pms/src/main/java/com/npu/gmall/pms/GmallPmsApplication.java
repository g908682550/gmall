package com.npu.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** 缓存的使用场景：
 * 一些固定的数据，不太变化的数据，包括高频访问的数据（不变），或者变化频率较低都可以入缓存，加速系统访问
 * 缓存的目的：提高系统查询效率，提供性能。
 * 1、将菜单缓存起来，以后查询直接去缓存中拿即可
 * 设计模式：模板模式
 * 操作xxx都有对应的Template；
 * JdbcTemplate、RestTemplate、RedisTemplate
 * RedisTemplate<Object, Object> k-v格式；v有五种类型
 * StringRedisTemplate：k-v都是String的。
 *
 * 引入场景，场景内的xxxAutoConfiguration，帮我们注入能操作这个技术的组件，这个场景的配置信息都在xxxProperties中，里面有说明使用哪种前缀
 * 2、整合Redis两大步
 *      1）导入场景启动器
 *      2）application.properties配置与spring.redis相关的
 *      注意：
 *          RedisTemplate：存数据默认使用jdk的方式序列化存过去
 *          推荐存成json数据，将默认的序列化器改成json
 * 3、如果事务加不上，开启基于注解的事务功能
 * 4、事务的最终解决方案：
 *      1、普通加事务，导入场景，加上相关注解
 *      2、方法自己调自己类里面的加不上事务
 *          1、导入aop包，开启代理对象的相关功能
 *          2、获取到当前类真正的代理对象，去调方法
 *              1、@EnableAspectJAutoProxy(exposeProxy = true):暴露代理对象
 *              2、获取代理对象；
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan(basePackages ="com.npu.gmall.pms.mapper" )
@EnableDubbo
@EnableRabbit
@EnableTransactionManagement
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
