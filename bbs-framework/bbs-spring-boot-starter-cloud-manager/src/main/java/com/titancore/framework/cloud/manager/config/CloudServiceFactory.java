package com.titancore.framework.cloud.manager.config;

import com.titancore.framework.cloud.manager.service.AliyunCloudStorageService;
import com.titancore.framework.cloud.manager.service.CloudService;
import com.titancore.framework.cloud.manager.service.QiniuCloudStorageService;
import com.titancore.framework.cloud.manager.service.TencentCloudStorageService;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import org.springframework.stereotype.Component;

public class CloudServiceFactory {

    private final CloudProperties properties;

    public CloudServiceFactory(CloudProperties properties) {
        this.properties = properties;
    }

    public CloudService createService() {
        String provider = properties.getProvider();
        switch (provider) {
            case "aliyun":
                return new AliyunCloudStorageService(properties);
            case "qiniu":
                return new QiniuCloudStorageService(properties);
            case "tencent":
                return new TencentCloudStorageService(properties);
            default:
                throw new IllegalArgumentException("Unknown cloud storage provider: " + provider);
        }
    }
}
