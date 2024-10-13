package com.titancore.framework.cloud.manager.service;

import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 云服务统一接口
 */
public interface CloudService {

    /**
     * 发送短信
     * @param phoneNumber
     * @param verificationCode
     * @return
     */
    String sendMessage(String phoneNumber,String verificationCode);


    /**
     * 上传图片
     * 路径 userId/image/xxx/
     * @param file
     * @param folderName
     * @return
     */
     String uploadImage(MultipartFile file,String folderName,boolean isPrivate);

    /**
     * 上传文件 （不限定格式）
     * 路径 userId/file/ or 文件夹名
     * @param file
     * @param folderName
     * @return
     */
    String uploadFile(MultipartFile file,String folderName,boolean isPrivate);

    /**
     * 根据用户id查询用户上传的文件
     * 路径 userId/file/ 下的文件
     *
     * @param userId
     * @return
     */
    FileListVo queryFileListByUserId(long userId,boolean isPrivate);

    /**
     * 删除指定路径下的文件/文件夹（包含子文件/文件夹）
     *
     * @param fileDelDTO 包含要删除的路径信息的DTO对象
     * @return 删除结果
     */
    boolean deleteByPath(FileDelDTO fileDelDTO,boolean isPrivate);

    /**
     * todo
     * 下载指定路径下的文件
     * @param fileDownloadDTO
     * @return
     */
    @Deprecated
    Map<String, Object> exportOssFileInputStream (FileDownloadDTO fileDownloadDTO);

    /**
     * 创建文件临时访问URL
     * @param filePath
     * @param isPrivate
     * @return
     */
    String createTemplateUrlOfFile(String filePath,int expiresIn,boolean isPrivate);
}
