package com.titancore.framework.cloud.manager.config;

import com.titancore.framework.cloud.manager.service.AliyunCloudStorageService;
import com.titancore.framework.cloud.manager.service.CloudService;
import com.titancore.framework.cloud.manager.service.QiniuCloudStorageService;
import com.titancore.framework.cloud.manager.service.TencentCloudStorageService;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloudServiceFactory {

    private final CloudProperties properties;

    public CloudServiceFactory(CloudProperties properties) {
        this.properties = properties;
    }

    public CloudService createService() {
        String provider = properties.getProvider();
        switch (provider) {
            case "aliyun":
                log.info("=======阿里云服务载入=======");
                return new AliyunCloudStorageService(properties);
            case "qiniu":
                log.info("=======七牛云服务载入=======");
                return new QiniuCloudStorageService(properties);
            case "tencent":
                log.info("=======腾讯云服务载入=======");
                return new TencentCloudStorageService(properties);
            default:
                throw new IllegalArgumentException("Unknown cloud storage provider: " + provider);
        }
    }
}
