package com.npu.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** 缓存的使用场景：
 * 事务的最终解决方案：
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
