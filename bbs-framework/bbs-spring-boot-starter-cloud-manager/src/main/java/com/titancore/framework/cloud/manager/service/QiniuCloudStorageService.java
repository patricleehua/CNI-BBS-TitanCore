package com.titancore.framework.cloud.manager.service;


import com.aliyun.oss.model.OSSObjectSummary;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDowloadDTO;
import com.titancore.framework.cloud.manager.domain.entity.File;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


//todo
public class QiniuCloudStorageService implements CloudService {

    private final CloudProperties cloudProperties;
    public QiniuCloudStorageService(CloudProperties cloudProperties) {
        this.cloudProperties=cloudProperties;
    }

    /**
     * 发送短信
     * @param phoneNumber
     * @param verificationCode
     * @return
     */
    @Override
    public String sendMessage(String phoneNumber, String verificationCode) {
        return null;
    }
    /**
     * 上传用户头像
     * 路径为 userid/avatar/xxx-xxx-xxx.jpg
     *
     * @param file 文件
     * @param userId 用户id
     * @return
     */
    @Override
    public String uploadAvatar(byte[] file, Long userId) {
        return null;
    }
    /**
     * 上传文章封面背景
     * @param file
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public String uploadBackground(byte[] file, Long userId, Long articleId) {
        return null;
    }

    /**
     * 上传文件 （不限定格式）
     * 路径 userId/file/xxx.zip
     *
     * @param file
     * @param userId
     * @param objectName
     * @return
     */
    @Override
    public String uploadFile(byte[] file, Long userId,  String objectName) {
        return null;
    }


    /**
     * 根据用户id查询用户上传的文件
     * 路径 userId/file/ 下的文件
     *
     * @param userId
     * @return
     */
    @Override
    public FileListVo queryFileListByUserId(long userId) {

        return null;
    }

    /**
     * 删除指定路径下的文件/文件夹（包含子文件/文件夹）
     *
     * @param fileDelDTO 包含要删除的路径信息的DTO对象
     * @return 删除结果
     */
    @Override
    public boolean deleteByPath(FileDelDTO fileDelDTO) {
    return false;
    }
    /**
     * 下载指定路径下的文件
     *
     * @param fileDowloadDTO
     * @return 根据流返回
     */
    @Override
    public byte[] exportOssFile(FileDowloadDTO fileDowloadDTO) {
        return new byte[0];
    }

}
