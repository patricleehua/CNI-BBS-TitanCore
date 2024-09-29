package com.titancore.framework.cloud.manager.config;

import com.titancore.framework.cloud.manager.service.*;
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
        return getCloudService(provider);
    }
    public CloudService createService(String provider) {
        return getCloudService(provider);
    }

    private CloudService getCloudService(String provider) {
        return switch (provider) {
            case "aliyun" -> {
                log.info("=======阿里云服务载入=======");
                yield new AliyunCloudStorageServiceV2(properties);
            }
            case "qiniu" -> {
                log.info("=======七牛云服务载入=======");
                yield new QiniuCloudStorageService(properties);
            }
            case "tencent" -> {
                log.info("=======腾讯云服务载入=======");
                yield new TencentCloudStorageService(properties);
            }
            case "minio" -> {
                log.info("=======minio服务载入=======");
                yield null;
//                yield new MinioStorageService(properties);
            }
            default -> throw new IllegalArgumentException("Unknown cloud storage provider: " + provider);
        };
    }
}
