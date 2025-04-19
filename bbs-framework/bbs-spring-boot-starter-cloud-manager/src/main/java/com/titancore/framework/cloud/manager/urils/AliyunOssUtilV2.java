package com.titancore.framework.cloud.manager.urils;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Data
@AllArgsConstructor
@Slf4j
public class AliyunOssUtilV2 {
    private String endpoint;
    private String accessKeyId;
    private String accessSecret;
    private String bucketNameOpen;
    private String bucketNamePrivate;

    private OSS createOssClient(){


        return new OSSClientBuilder().build(endpoint,accessKeyId,accessSecret);
    }
    /**
     * 2024.9.30 pass
     * @param file
     * @param folderName 路径/文件夹名
     * @return
     */
    public String upload(MultipartFile file, String folderName,boolean isPrivate){
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
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
        OSS ossClient = createOssClient();
        try {
            ossClient.putObject(bucketName, fullObjectName, file.getInputStream());
        } catch (OSSException  oe) {
            log.error(
    """
    Caught an OSSException, which means your request made it to OSS,
    but was rejected with an error response for some reason.
    """
            );
            log.error("Error Message: {}", oe.getErrorMessage());
            log.error("Error Code: {}", oe.getErrorCode());
            log.error("Request ID: {}", oe.getRequestId());
            log.error("Host ID: {}", oe.getHostId());

        } catch (ClientException ce) {
            log.error(
    """
    Caught an ClientException, which means the client encountered
    a serious internal problem while trying to communicate with OSS,
    such as not being able to access the network.
    """
            );
            log.error("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(fullObjectName);
        log.info("文件上传到:{}", stringBuilder);
        return stringBuilder.toString();
    }


    /**
     * 查询 路径 Path/ 下的文件
     * 2024.11.12 增加对url携带域名的处理
     * @param path
     * @return
     */
    public List<OSSObjectSummary> queryFileListByPath(String path,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        path = FileUtil.extractPathAfterKeyword(path,"aliyuncs.com/");
        //创建OSSClient实例
        OSS ossClient = createOssClient();
        List<OSSObjectSummary> sums = null;
        try{
            ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withPrefix(String.valueOf(path)));
            sums = objectListing.getObjectSummaries();
        }catch (OSSException oe) {
            log.error(
                    """
                    Caught an OSSException, which means your request made it to OSS,
                    but was rejected with an error response for some reason.
                    """
            );
            log.error("Error Message: {}", oe.getErrorMessage());
            log.error("Error Code: {}", oe.getErrorCode());
            log.error("Request ID: {}", oe.getRequestId());
            log.error("Host ID: {}", oe.getHostId());
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return sums;
    }


    /**
     * 2024.9.30 pass
     * 2024.11.12 增加对url携带域名的处理
     * 删除指定单个文件
     * @param filePath
     * @return
     */

    public boolean deleteFile(String filePath,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        filePath = FileUtil.extractPathAfterKeyword(filePath,"aliyuncs.com/");
        OSS ossClient = createOssClient();
        try {
            VoidResult voidResult = ossClient.deleteObject(bucketName, filePath);
            log.info("Deleted file: {}", filePath);
            return true;
        }catch (OSSException oe) {
            log.error(
                    """
                    Caught an OSSException, which means your request made it to OSS,
                    but was rejected with an error response for some reason.
                    """
            );
            log.error("Error Message: {}", oe.getErrorMessage());
            log.error("Error Code: {}", oe.getErrorCode());
            log.error("Request ID: {}", oe.getRequestId());
            log.error("Host ID: {}", oe.getHostId());
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 2024.9.30 pass
     * 2024.11.12 增加对url携带域名的处理
     * 删除指定的整个目录
     * @param dirPath
     * @return
     */
    public boolean deleteDirectory(String dirPath,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        dirPath = FileUtil.extractPathAfterKeyword(dirPath,"aliyuncs.com/");
        OSS ossClient = createOssClient();
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                    .withPrefix(dirPath);
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
            List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();

            for (OSSObjectSummary objectSummary : objectSummaries) {
                String key = objectSummary.getKey();
                ossClient.deleteObject(bucketName, key);
                log.info("Deleted file of directory: {}", key);
            }
            return true;
        }catch (OSSException oe) {
            log.error(
                    """
                    Caught an OSSException, which means your request made it to OSS,
                    but was rejected with an error response for some reason.
                    """
            );
            log.error("Error Message: {}", oe.getErrorMessage());
            log.error("Error Code: {}", oe.getErrorCode());
            log.error("Request ID: {}", oe.getRequestId());
            log.error("Host ID: {}", oe.getHostId());
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 2024.9.30 不建议使用，仅供参考
     * 2024.11.12 增加对url携带域名的处理
     * todo 问题: 在客户端执行取消后，阿里云OSS的流还是被继续访问，直到传完！这个一个非常严重的bug
     * @author gaojun
     * @desc 下载文件
     * 文档链接 https://help.aliyun.com/document_detail/84823.html?spm=a2c4g.11186623.2.7.37836e84ZIuZaC#concept-84823-zh
     * @email
     */
    @Deprecated
    public Map<String, Object> exportOssFile(String Path,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        Path = FileUtil.extractPathAfterKeyword(Path,"aliyuncs.com/");
        //创建OSSClient实例
        OSS ossClient = createOssClient();
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(bucketName, Path);
        // 获取文件大小
        long fileSize = ossObject.getObjectMetadata().getContentLength();
        InputStream is = ossObject.getObjectContent();

        // 将输入流和文件大小放入Map
        Map<String, Object> result = new HashMap<>();
        result.put("inputStream", is);
        result.put("fileSize", fileSize);
        return result;
    }

    /**
     * 2024.9.30 pass
     * 2024.11.12 增加对url携带域名的处理
     * 获取指定路径文件的临时URL
     * @param filePath
     * @param expiresIn
     * @param isPrivate
     * @return
     */
    public String getTemplateUrl(String filePath,int expiresIn,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        filePath = FileUtil.extractPathAfterKeyword(filePath, "aliyuncs.com/");
        OSS ossClient = createOssClient();
        try {
            Date expireTime = new Date(System.currentTimeMillis() + expiresIn * 24 * 3600 * 1000);
            URL url = ossClient.generatePresignedUrl(bucketName, filePath, expireTime);
            return url.toString();
        }catch (ClientException ce){
            log.error(
                    """
                    Caught an ClientException, which means your request made it to OSS,
                    but was rejected with an error response for some reason.
                    """
            );
            log.error("Error Message: {}", ce.getErrorMessage());
            log.error("Error Code: {}", ce.getErrorCode());
            log.error("Request ID: {}", ce.getRequestId());
            return "";
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * un pass 2024 11 13
     * 断点下载
     * @param filePath
     * @param offset
     * @param length
     * @param isPrivate
     * @return
     */
    public InputStream getObject(String filePath,long offset, long length,boolean isPrivate) {
        //是否私有
        String bucketName = isPrivate ? bucketNamePrivate:bucketNameOpen;
        filePath = FileUtil.extractPathAfterKeyword(filePath, "aliyuncs.com/");
        OSS ossClient = createOssClient();
        try {
            // 创建 GetObjectRequest 对象，并设置 Range 参数
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filePath);
            getObjectRequest.setRange(offset, offset + length - 1);  // 设置下载范围

            // 下载对象并返回输入流
            OSSObject ossObject = ossClient.getObject(getObjectRequest);
            // todo 这个先存本地，然后在返回，这个有问题，不可用！！
            try (InputStream inputStream = ossObject.getObjectContent()) {
                byte[] data = inputStream.readAllBytes();
                return new ByteArrayInputStream(data);  // 返回字节数组的流
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }catch (ClientException ce){
            log.error(
                    """
                    Caught an ClientException, which means your request made it to OSS,
                    but was rejected with an error response for some reason.
                    """
            );
            log.error("Error Message: {}", ce.getErrorMessage());
            log.error("Error Code: {}", ce.getErrorCode());
            log.error("Request ID: {}", ce.getRequestId());
            return null;
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
