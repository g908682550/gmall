package com.npu.gmall.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "gmall.pool")
@Configuration
public class PoolProperties {

    private Integer coreSize;

    private Integer maximumPoolSize;

    private Integer queueSize;
}
