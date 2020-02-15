package com.npu.gmall.portal;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.xml.crypto.Data;

/**
 * 数组类型的数据提交，需要同名属性多几个值就行，如
 * "http://localhost:8080/search?catelog3=19&catelog3=20&catelog3=30&keyword=%E6%89%8B%E6%9C%BA"
 */
@EnableRabbit
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GmallShopPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallShopPortalApplication.class, args);
    }

}
