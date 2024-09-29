package com.titancore.framework.cloud.manager.service;

import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;

import java.io.InputStream;
import java.util.Map;

//云服务统一接口
public interface CloudService {

    /**
     * 发送短信
     * @param phoneNumber
     * @param verificationCode
     * @return
     */
    String sendMessage(String phoneNumber,String verificationCode);

    /**
     * 上传用户头像
     * 路径为 userid/avatar/xxx-xxx-xxx.jpg
     *
     * @param file 文件
     * @param userId 用户id
     * @return
     */
     String uploadAvatar(byte[]  file,Long userId);

    /**
     * 上传文章封面背景
     * 路径  userId/article/background/articleId/xxx-xxx-xxx.jpg
     *
     * @param file
     * @param userId
     * @param articleId
     * @return
     */
    String uploadBackground(byte[]  file,Long userId,Long articleId);


    /**
     * 上传文件 （不限定格式）
     * 路径 userId/file/xxx.zip
     *
     * @param file
     * @param userId
     * @param objectName
     * @return
     */
    String uploadFile(byte[]  file,Long userId,String objectName);

    /**
     * 根据用户id查询用户上传的文件
     * 路径 userId/file/ 下的文件
     *
     * @param userId
     * @return
     */
    FileListVo queryFileListByUserId(long userId);

    /**
     * 删除指定路径下的文件/文件夹（包含子文件/文件夹）
     *
     * @param fileDelDTO 包含要删除的路径信息的DTO对象
     * @return 删除结果
     */
    boolean deleteByPath(FileDelDTO fileDelDTO);

    /**
     * 下载指定路径下的文件
     *
     * @param fileDownloadDTO
     * @return 根据流返回
     */
    byte[] exportOssFile(FileDownloadDTO fileDownloadDTO);
    Map<String, Object> exportOssFileInputStream (FileDownloadDTO fileDownloadDTO);

}
