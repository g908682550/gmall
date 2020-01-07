package com.npu.gmall.search;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableDubbo
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallSearchApplication.class, args);
    }

}
