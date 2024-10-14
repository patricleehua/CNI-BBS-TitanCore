package com.titancore.framework.cloud.manager.urils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@Slf4j
public class MinioUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessSecret;
    private String bucketNameOpen;
    private String bucketNamePrivate;
    private MinioClient createMinioClient(){
        return  MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKeyId, accessSecret)
                .build();
    }
    /**
     * 2024.9.30 pass
     * 文件上传
     * @param file
     * @param folderName 路径/文件夹名
     * @return
     */
    public String upload(MultipartFile file, String folderName,boolean isPrivate){
        String bucketName = isPrivate?bucketNamePrivate:bucketNameOpen;
        //如果是图片则重命名
        String fileName;
        if(FileUtil.isImage(file)){
            fileName = FileUtil.renameFileToUUID(file);
        }else{
            fileName = file.getOriginalFilename();
        }
        //上传指定文件夹：
        String fullObjectName = folderName + "/" + fileName;
        //创建OSSClient实例
        // 构建 Minio 客户端
        MinioClient minioClient = createMinioClient();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullObjectName)
                    .stream(file.getInputStream(), file.getSize(),-1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            log.error("Minio: create template url for file is error!");
            log.error("This is cache exception message:{}",e.toString());
            return "";
        } finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }
        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(endpoint)
                .append("/")
                .append(bucketName)
                .append("/")
                .append(fullObjectName);
        log.info("文件上传到:{}", stringBuilder);
        return stringBuilder.toString();
    }

//    /**
//     * 上传文件
//     * @param file
//     * @return
//     * @throws Exception
//     */
//    public String uploadFile(MultipartFile file) throws Exception {
//        // 判断文件是否为空
//        if (file == null || file.getSize() == 0) {
//            log.error("==> 上传文件异常：文件大小为空 ...");
//            throw new RuntimeException("文件大小不能为空");
//        }
//
//        // 文件的原始名称
//        String originalFileName = file.getOriginalFilename();
//        // 文件的 Content-Type
//        String contentType = file.getContentType();
//
//        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
//        String key = UUID.randomUUID().toString().replace("-", "");
//        // 获取文件的后缀，如 .jpg
//        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
//
//        // 拼接上文件后缀，即为要存储的文件名
//        String objectName = String.format("%s%s", key, suffix);
//
//        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);
//
//        // 上传文件至 Minio
//        minioClient.putObject(PutObjectArgs.builder()
//                .bucket(minioProperties.getBucketName())
//                .object(objectName)
//                .stream(file.getInputStream(), file.getSize(), -1)
//                .contentType(contentType)
//                .build());
//
//        // 返回文件的访问链接
//        String url = String.format("%s/%s/%s", minioProperties.getEndpoint(), minioProperties.getBucketName(), objectName);
//        log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
//        return url;
//    }

    /**
     * 2024.9.30 pass
     * 文件的临时URL地址
     * @param filePath
     * @param expiresIn
     * @param isPrivate
     * @return
     */
    public String getTemplateUrl(String filePath,int expiresIn,boolean isPrivate){
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;

        MinioClient minioClient = createMinioClient();
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filePath)
                            .expiry(expiresIn, TimeUnit.HOURS)
                            .build()
            );
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: Upload file to Minio is error!");
            log.error("This is cache exception message:{}",e.toString());
            return "";
        } finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }
    }
}
