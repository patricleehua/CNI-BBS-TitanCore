package com.titancore.framework.cloud.manager.service;


import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import com.titancore.framework.cloud.manager.urils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Slf4j
public class MinioStorageService implements CloudService {

    private final CloudProperties cloudProperties;
    public MinioStorageService(CloudProperties cloudProperties) {
        this.cloudProperties=cloudProperties;
    }


    private MinioUtil initOss(){
        return new MinioUtil(
                cloudProperties.getMinio().getEndpoint(),
                cloudProperties.getMinio().getAccessKeyId(),
                cloudProperties.getMinio().getAccessKeySecret(),
                cloudProperties.getMinio().getBucketNameOpen(),
                cloudProperties.getMinio().getBucketNamePrivate()
        );
    }


    /**
     * 发送短信 本地不支持！
     * @param phoneNumber
     * @param verificationCode
     * @return
     */
    @Override
    @Deprecated(since = "本地环境不支持调用")
    public String sendMessage(String phoneNumber, String verificationCode) {
        return null;
    }

    @Override
    public String uploadImage(MultipartFile file, String filePath,boolean isPrivate) {
        return initOss().upload(file,filePath,isPrivate);
    }


    @Override
    @Deprecated
    public Map<String, Object> exportOssFileInputStream(FileDownloadDTO fileDownloadDTO) {
        return null;
    }

    @Override
    public String createTemplateUrlOfFile(String filePath,int expiresIn,boolean isPrivate) {
        return initOss().getTemplateUrl(filePath,expiresIn,isPrivate);
    }

    @Override
    public String uploadFile(MultipartFile file, String folderName,boolean isPrivate) {
        return initOss().upload(file, folderName,isPrivate);
    }

    @Override
    public FileListVo queryFileListByUserId(long userId,boolean isPrivate) {
        return null;
    }

    @Override
    public boolean deleteByPath(FileDelDTO fileDelDTO,boolean isPrivate) {
        return false;
    }


}
