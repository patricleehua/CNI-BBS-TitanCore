package com.titancore.framework.cloud.manager.urils;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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
     * 使用MultipartFile进行文件上传
     * @param file
     * @param folderName 路径/文件夹名
     * @return
     */
    public String uploadFile(MultipartFile file, String folderName,boolean isPrivate){
        String bucketName = isPrivate?bucketNamePrivate:bucketNameOpen;
        //如果是图片则重命名
        String fileName;
        if(FileUtil.isImage(file)){
            fileName = FileUtil.renameFileToUUID(file);
        }else{
            fileName = file.getOriginalFilename();
        }
        //上传指定文件夹：
        String fullObjectName = folderName + fileName;
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
            log.error("Minio: Using MultipartFile upload to minio is error!");
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

    /**
     * 2024.9.30 pass
     * 2024.11.12 增加对url携带域名的处理
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
            filePath = FileUtil.extractPathAfterKeyword(filePath, bucketName);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filePath)
                            .expiry(expiresIn, TimeUnit.HOURS)
                            .build()
            );
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: get template url from file is error!");
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
    /**
     * 2024.11.6 pass
     * 删除指定的文件
     * @param filePath
     * @param isPrivate
     * @return
     */
    public boolean deleteFile(String filePath,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        MinioClient minioClient = createMinioClient();
        try {
            RemoveObjectArgs removeArgs = RemoveObjectArgs.builder().bucket(bucketName).object(filePath).build();
            minioClient.removeObject(removeArgs);
            log.info("Deleted file: {}", filePath);
            return true;
        }catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: Delete file from Minio failed!");
            log.error("This is cache exception message:{}",e.toString());
            return false;
        } finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }
    }

    /**
     * 2024.11.6 pass
     * 2024.11.12 增加对url携带域名的处理
     * 删除指定的整个目录
     * @param dirPath
     * @param isPrivate
     * @return
     */
    public boolean deleteDirectory(String dirPath,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        MinioClient minioClient = createMinioClient();
        try {
            dirPath = FileUtil.extractPathAfterKeyword(dirPath, bucketName);
            List<Item> items = queryFileListByPath(dirPath, true, isPrivate);
            if (items == null || items.isEmpty()) {
                log.info("Directory is empty or does not exist.");
                return true;
            }
            // 构建删除对象列表
            List<DeleteObject> objectsToDelete = new ArrayList<>();
            for (Item item : items) {
                objectsToDelete.add(new DeleteObject(item.objectName()));
            }
            // 删除对象
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objectsToDelete)
                            .build()
            );
            // 检查是否有删除错误
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error deleting object: " + error.objectName() + ", " + error.message());
            }
            return true;
        }catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: Deleting file to Minio is error!");
            log.error("This is cache exception message:{}",e.toString());
            return false;
        } finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }
    }
    /**
     * 2024.11.6 pass
     * 2024.11.12 增加对url携带域名的处理
     * 获取路径下文件列表
     * @param prefix 文件名称路径
     * @param recursive 是否递归查找，false：模拟文件夹结构查找 true:递归查找，如果有子文件夹，则返回包含子文件夹的内容
     * @param isPrivate
     * @return
     */
    public List<Item> queryFileListByPath(String prefix, boolean recursive, boolean isPrivate) {
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        MinioClient minioClient = createMinioClient();
        try{
            prefix = FileUtil.extractPathAfterKeyword(prefix, bucketName);
            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build();
            Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            ArrayList<Item> items = new ArrayList<>();
            for (Result<Item> result : results) {
                items.add(result.get());
            }
            return items;
        }catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: Query List for Dir from Minio is error!");
            log.error("This is cache exception message:{}",e.toString());
            return null;
        }finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }
    }
    /**
     * 2024.11.6 pass
     * 判断Bucket是否存在，true：存在，false：不存在
     *
     * @param bucketName
     * @return
     */
    public boolean bucketExists(String bucketName) {
        MinioClient minioClient = createMinioClient();
        try{
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        }catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: bucketExists from Minio is error!");
            log.error("This is cache exception message:{}",e.toString());
            return false;
        }finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }
    }
    /**
     * 2024.11.6 pass
     * 2024.11.12 增加对url携带域名的处理
     * 拷贝文件
     *
     * @param bucketName    存储桶
     * @param objectName    文件名
     * @param srcBucketName 目标存储桶
     * @param srcObjectName 目标文件名
     */
    public ObjectWriteResponse copyFile(String bucketName, String objectName, String srcBucketName, String srcObjectName) {
        MinioClient minioClient = createMinioClient();
        try{
            objectName = FileUtil.extractPathAfterKeyword(objectName, bucketName);
            return minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build())
                    .bucket(srcBucketName)
                    .object(srcObjectName)
                    .build());
        }catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: copyFile from Minio is error!");
            log.error("This is cache exception message:{}",e.toString());
            return null;
        }finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }
    }
    /**
     * 断点下载
     *
     * @param objectName 文件名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度
     * @return 二进制流
     */
    public InputStream getObject(String objectName, long offset, long length, boolean isPrivate) {
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        MinioClient minioClient = createMinioClient();
        try{
            objectName = FileUtil.extractPathAfterKeyword(objectName, bucketName);
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .offset(offset)
                    .length(length)
                    .build());

        }catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Minio: copyFile from Minio is error!");
            log.error("This is cache exception message:{}",e.toString());
            return null;
        }finally {
            try {
                minioClient.close();
            } catch (Exception e) {
                log.error("Minio: Close minio connect is error!");
                log.error("This is cache exception message:{}",e.toString());
            }
        }

    }



}
