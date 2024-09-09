package com.titancore.framework.cloud.manager.config;

import com.titancore.framework.cloud.manager.service.AliyunCloudStorageService;
import com.titancore.framework.cloud.manager.service.CloudService;
import com.titancore.framework.cloud.manager.service.QiniuCloudStorageService;
import com.titancore.framework.cloud.manager.service.TencentCloudStorageService;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudStorageAutoConfiguration {
    @Bean(name = "aliyunCloudStorageService")
    @ConditionalOnProperty(name = "titan.cloud.provider", havingValue = "aliyun", matchIfMissing = false)
    public CloudService aliyunCloudStorageService(CloudProperties cloudProperties) {
        return new AliyunCloudStorageService(cloudProperties);
    }

    @Bean(name = "qiniuCloudStorageService")
    @ConditionalOnProperty(name = "titan.cloud.provider", havingValue = "qiniu", matchIfMissing = false)
    public CloudService qiniuCloudStorageService(CloudProperties cloudProperties) {
        return new QiniuCloudStorageService(cloudProperties);
    }

    @Bean(name = "tencentCloudStorageService")
    @ConditionalOnProperty(name = "titan.cloud.provider", havingValue = "tencent", matchIfMissing = false)
    public CloudService tencentCloudStorageService(CloudProperties cloudProperties) {
        return new TencentCloudStorageService(cloudProperties);
    }

    @Bean(name = "cloudServiceFactory")
    public CloudServiceFactory cloudServiceFactory(CloudProperties cloudProperties) {
        return new CloudServiceFactory(cloudProperties);
    }
}
