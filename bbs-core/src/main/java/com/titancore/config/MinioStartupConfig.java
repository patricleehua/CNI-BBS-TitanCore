package com.titancore.config;

import com.titancore.framework.cloud.manager.properties.CloudProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * MinIO启动检查配置
 * 在应用启动时检查并创建必要的bucket
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CloudProperties.class)
@ConditionalOnProperty(prefix = "titan.cloud", name = "provider", havingValue = "minio")
public class MinioStartupConfig {

    private final CloudProperties cloudProperties;

    public MinioStartupConfig(CloudProperties cloudProperties) {
        this.cloudProperties = cloudProperties;
    }

    @PostConstruct
    public void initMinIOBuckets() {
        CloudProperties.Minio minioConfig = cloudProperties.getMinio();

        if (minioConfig == null) {
            log.warn("MinIO配置未找到,跳过bucket初始化");
            return;
        }

        String endpoint = minioConfig.getEndpoint();
        String accessKeyId = minioConfig.getAccessKeyId();
        String accessKeySecret = minioConfig.getAccessKeySecret();
        String publicBucket = minioConfig.getBucketNameOpen();
        String privateBucket = minioConfig.getBucketNamePrivate();

        if (endpoint == null || accessKeyId == null || accessKeySecret == null) {
            log.warn("MinIO连接配置不完整,跳过bucket初始化");
            return;
        }

        MinioClient minioClient = null;
        try {
            // 创建MinIO客户端
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKeyId, accessKeySecret)
                    .build();

            // 检查并创建公有bucket
            if (publicBucket != null && !publicBucket.isEmpty()) {
                checkAndCreateBucket(minioClient, publicBucket, true);
            }

            // 检查并创建私有bucket
            if (privateBucket != null && !privateBucket.isEmpty()) {
                checkAndCreateBucket(minioClient, privateBucket, false);
            }

            log.info("MinIO bucket初始化检查完成");

        } catch (Exception e) {
            log.error("MinIO bucket初始化失败: {}", e.getMessage(), e);
        } finally {
            if (minioClient != null) {
                try {
                    minioClient.close();
                } catch (Exception e) {
                    log.error("关闭MinIO连接失败: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 检查并创建bucket
     *
     * @param minioClient MinIO客户端
     * @param bucketName  bucket名称
     * @param isPublic    是否为公有bucket
     */
    private void checkAndCreateBucket(MinioClient minioClient, String bucketName, boolean isPublic) {
        try {
            // 检查bucket是否存在
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (exists) {
                log.info("Bucket [{}] 已存在,跳过创建", bucketName);
            } else {
                // 创建bucket
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("成功创建Bucket [{}]", bucketName);

                // 设置bucket策略
                if (isPublic) {
                    setPublicBucketPolicy(minioClient, bucketName);
                } else {
                    log.info("Bucket [{}] 设置为私有访问", bucketName);
                }
            }
        } catch (Exception e) {
            log.error("处理Bucket [{}] 时发生错误: {}", bucketName, e.getMessage(), e);
        }
    }

    /**
     * 设置bucket为公有访问策略
     *
     * @param minioClient MinIO客户端
     * @param bucketName  bucket名称
     */
    private void setPublicBucketPolicy(MinioClient minioClient, String bucketName) {
        try {
            // 构建公有访问策略 - 允许匿名读取
            String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {"AWS": ["*"]},
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }
                    """.formatted(bucketName);

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policy)
                            .build()
            );
            log.info("Bucket [{}] 已设置为公有访问", bucketName);
        } catch (Exception e) {
            log.error("设置Bucket [{}] 公有策略失败: {}", bucketName, e.getMessage(), e);
        }
    }
}
