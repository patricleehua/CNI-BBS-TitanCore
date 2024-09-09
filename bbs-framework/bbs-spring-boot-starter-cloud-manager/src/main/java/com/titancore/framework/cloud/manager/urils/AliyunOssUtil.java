package com.titancore.framework.cloud.manager.urils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class AliyunOssUtil {
    private String endpoint;
    private String accessKeyId;
    private String accessSecret;
    private String bucketName;

    /**
     * 上传
     * @param bytes 字节数组
     * @param folderName 路径/文件夹名
     * @param objectName 文件名
     * @return
     */
    public String upload(byte[] bytes,String folderName,String objectName){
        //上传指定文件夹：
        String fullObjectName = folderName + "/" + objectName;
        //创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessSecret);
        try {
            ossClient.putObject(bucketName, fullObjectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
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
        log.info("文件上传到:{}", stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 根据用户id查询用户上传的文件
     * 路径 userId/file/ 下的文件
     *
     * @param userId
     * @return
     */
    public List<OSSObjectSummary> queryFileListByUserId(Long userId) {
        //创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessSecret);
        String fileNamePath = userId+"/file";
        List<OSSObjectSummary> sums = null;
        try{
            ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withPrefix(String.valueOf(fileNamePath)));
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
     * 删除单个文件
     * @param filePath
     * @return
     */

    public boolean deleteFile(String filePath) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessSecret);
        try {
            ossClient.deleteObject(bucketName, filePath);
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
     * 删除整个目录
     * @param dirPath
     * @return
     */
    public boolean deleteDirectory(String dirPath) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessSecret);
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
     * @author gaojun
     * @desc 下载文件
     * 文档链接 https://help.aliyun.com/document_detail/84823.html?spm=a2c4g.11186623.2.7.37836e84ZIuZaC#concept-84823-zh
     * @email
     */
    public
    byte[] exportOssFile(String Path) {
        //创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessSecret);
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(bucketName, Path);
        InputStream is = ossObject.getObjectContent();
        // 读取文本文件内容并转为字节数组
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = readBytesFromInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ossClient != null) {
            ossClient.shutdown();
        }
        return fileBytes;
    }

    // 读取文本文件内容并转为字节数组
    private byte[] readBytesFromInputStream(InputStream is) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }
}
