package com.titancore.framework.cloud.manager.service;


import com.aliyun.oss.model.OSSObjectSummary;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDowloadDTO;
import com.titancore.framework.cloud.manager.domain.entity.File;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import com.titancore.framework.cloud.manager.urils.AliyunOssUtil;
import com.titancore.framework.cloud.manager.urils.AliyunSmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
public class AliyunCloudStorageService implements CloudService {

    private final CloudProperties cloudProperties;
    public AliyunCloudStorageService(CloudProperties cloudProperties) {
        this.cloudProperties=cloudProperties;
    }
    @Value("${titan.cloud.maxSize}")
    private int maxSize;

    private AliyunSmsUtils initSms(){
        return new  AliyunSmsUtils(
                cloudProperties.getAliyun().getAsms().getRegionId(),
                cloudProperties.getAliyun().getAsms().getAccessKeyId(),
                cloudProperties.getAliyun().getAsms().getAccessKeySecret(),
                cloudProperties.getAliyun().getAsms().getTemplateCode(),
                cloudProperties.getAliyun().getAsms().getSignName()
        );
    }
    private AliyunOssUtil initOss(){
        return new  AliyunOssUtil(
                cloudProperties.getAliyun().getAoss().getEndpoint(),
                cloudProperties.getAliyun().getAoss().getAccessKeyId(),
                cloudProperties.getAliyun().getAoss().getAccessKeySecret(),
                cloudProperties.getAliyun().getAoss().getBucketName()
        );
    }


    /**
     * 发送短信
     * @param phoneNumber
     * @param verificationCode
     * @return
     */
    @Override
    public String sendMessage(String phoneNumber, String verificationCode) {
        return  initSms().sendMessage(phoneNumber, verificationCode);
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
    public String uploadAvatar(byte[]  file,Long userId) {
//        double byteSize = file.length / (1024*1024);
//        if (byteSize > maxSize) {
//            throw new BizException(ResponseCodeEnum.UPLOAD_BIG);
//        }
        String extension = ".jpg";
        String objectName = UUID.randomUUID()+ extension;
        String folderName = userId + "/avatar";

        return initOss().upload(file, folderName,objectName);
    }

    /**
     * 上传文章封面背景
     * 路径  userId/article/background/articleId/xxx-xxx-xxx.jpg
     *
     * @param file
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public String uploadBackground(byte[] file, Long userId, Long articleId) {
        String extension = ".jpg";
        String objectName = UUID.randomUUID()+ extension;

        String folderName = userId + "/article/background/"+articleId;
        return initOss().upload(file, folderName,objectName);
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
        String folderName = userId + "/file";
        return initOss().upload(file,folderName,objectName);
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
        List<OSSObjectSummary> ossObjectSummaries = initOss().queryFileListByUserId(userId);
        List<File> files= new ArrayList<>();
        for (OSSObjectSummary file : ossObjectSummaries) {
            String fname=file.getKey();
            // 找到最后一个斜杠的位置
            int lastSlashIndex = fname.lastIndexOf('/');

// 使用substring截取从最后一个斜杠位置开始到字符串末尾的部分
            String result = fname.substring(lastSlashIndex + 1);
            File f = new File();
            f.setFileSize(String.valueOf(file.getSize()));
            f.setUploadTime(file.getLastModified().toString());
            f.setFileName(result);
            files.add(f);
        }
        FileListVo fileListVo = new FileListVo();
        fileListVo.setFiles(files);
        fileListVo.setUserId(String.valueOf(userId));
//        fileListVo.setUserName(user.getUserName());

        return fileListVo;
    }

    /**
     * 删除指定路径下的文件/文件夹（包含子文件/文件夹）
     *
     * @param fileDelDTO 包含要删除的路径信息的DTO对象
     * @return 删除结果
     */
    @Override
    public boolean deleteByPath(FileDelDTO fileDelDTO) {
        String path = fileDelDTO.getUserId() + "/" + fileDelDTO.getPath();
        if (fileDelDTO.getFileName() != null && !fileDelDTO.getFileName().isEmpty()) {
            // 删除单个文件
            return initOss().deleteFile(path + fileDelDTO.getFileName());
        } else {
            // 删除整个目录
            return initOss().deleteDirectory(path);
        }
    }
    /**
     * 下载指定路径下的文件
     *userId/file/xxx.zip
     *
     * @param fileDowloadDTO
     * @return 根据流返回
     */
    @Override
    public byte[] exportOssFile(FileDowloadDTO fileDowloadDTO) {
        String Path=fileDowloadDTO.getUserId()+"/file/"+fileDowloadDTO.getFileName();
        return initOss().exportOssFile(Path);
    }


}
