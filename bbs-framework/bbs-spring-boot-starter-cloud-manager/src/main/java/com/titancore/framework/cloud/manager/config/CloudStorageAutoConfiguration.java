package com.titancore.framework.cloud.manager.config;


import com.titancore.framework.cloud.manager.properties.CloudProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@RefreshScope cloud使用：应用配置发生变化时， Bean 可以重新加载这些配置。
public class CloudStorageAutoConfiguration {

    @Bean(name = "cloudServiceFactory")
    public CloudServiceFactory cloudServiceFactory(CloudProperties cloudProperties) {
        return new CloudServiceFactory(cloudProperties);
    }
}
