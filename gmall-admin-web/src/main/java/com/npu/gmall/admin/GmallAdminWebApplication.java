package com.npu.gmall.admin;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * VO(View Object/Value Object)视图对象：
 *      1）、List<User>:把专门交给前端的数据封装成VO
 *      2）、用户给我提交的封装成VO往下传
 *      request--->提交的vo；
 *      response--->返回的vo；
 * TO（传输对象）
 *@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
 * 1、不进行数据源的自动配置
 *
 * 如果导入的依赖，引入一个自动配置场景
 * 1、这个场景自动配置默认生效
 * 2、如果不想配置 1.引入的时候排除场景依赖  2.排除调这个场景的自动配置类
 *
 */
@EnableDubbo
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GmallAdminWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallAdminWebApplication.class, args);
    }

}
