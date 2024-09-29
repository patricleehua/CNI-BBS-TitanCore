package com.titancore.framework.cloud.manager.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "titan.cloud")
@Data
public class CloudProperties {
    private String provider;
    private Aliyun aliyun;
    private Qiniu qiniu;
    private Tencent tencent;
    private Minio minio;

    @Getter
    @Setter
    public static class Aliyun {
        private Aoss aoss;
        private Asms asms;

        @Getter
        @Setter
        public static class Aoss {
            private String endpoint;
            private String accessKeyId;
            private String accessKeySecret;
            private String bucketName;
        }

        @Getter
        @Setter
        public static class Asms {
            private String regionId;
            private String accessKeyId;
            private String accessKeySecret;
            private String templateCode;
            private String signName;
        }
    }

    @Getter
    @Setter
    public static class Qiniu {
        private String accessKey;
        private String secretKey;
        private String bucketName;
        private String domain;
    }

    @Getter
    @Setter
    public static class Tencent {
        private String secretId;
        private String secretKey;
        private String region;
        private String bucketName;
    }
    @Getter
    @Setter
    public static class Minio {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
    }
}
