package com.titancore.framework.cloud.manager.service;


import com.aliyun.oss.model.OSSObjectSummary;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.cloud.manager.domain.entity.File;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import com.titancore.framework.cloud.manager.urils.AliyunOssUtilV2;
import com.titancore.framework.cloud.manager.urils.AliyunSmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class AliyunCloudStorageService implements CloudService {

    private final CloudProperties cloudProperties;
    public AliyunCloudStorageService(CloudProperties cloudProperties) {
        this.cloudProperties=cloudProperties;
    }


    private AliyunSmsUtils initSms(){
        return new  AliyunSmsUtils(
                cloudProperties.getAliyun().getAsms().getRegionId(),
                cloudProperties.getAliyun().getAsms().getAccessKeyId(),
                cloudProperties.getAliyun().getAsms().getAccessKeySecret(),
                cloudProperties.getAliyun().getAsms().getTemplateCode(),
                cloudProperties.getAliyun().getAsms().getSignName()
        );
    }
    private AliyunOssUtilV2 initOss(){
        return new AliyunOssUtilV2(
                cloudProperties.getAliyun().getAoss().getEndpoint(),
                cloudProperties.getAliyun().getAoss().getAccessKeyId(),
                cloudProperties.getAliyun().getAoss().getAccessKeySecret(),
                cloudProperties.getAliyun().getAoss().getBucketNameOpen(),
                cloudProperties.getAliyun().getAoss().getBucketNamePrivate()
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

    @Override
    public String uploadImage(MultipartFile file, String filePath,boolean isPrivate) {
        return initOss().upload(file, filePath, isPrivate);
    }

    @Override
    public String uploadFile(MultipartFile file, String folderName,boolean isPrivate) {
        return initOss().upload(file,folderName,isPrivate);
    }

    /**
     * 删除指定路径下的文件/文件夹（包含子文件/文件夹）
     *
     * @param fileDelDTO 包含要删除的路径信息的DTO对象
     * @return 删除结果
     */
    @Override
    public boolean deleteByPath(FileDelDTO fileDelDTO,boolean isPrivate) {
        String path = fileDelDTO.getPath();
        if (fileDelDTO.getFileName() != null && !fileDelDTO.getFileName().isEmpty()) {
            path = fileDelDTO.getPath().endsWith("/") ? path : path + "/";
            // 删除单个文件
            return initOss().deleteFile(path + fileDelDTO.getFileName(),isPrivate);
        } else {
            // 删除整个目录
            return initOss().deleteDirectory(path,isPrivate);
        }
    }

    @Override
    @Deprecated
    public Map<String, Object> exportOssFileInputStream(FileDownloadDTO fileDownloadDTO) {
        boolean isPrivate = fileDownloadDTO.getIsPrivate().equals("0");
        String Path= fileDownloadDTO.getUserId()+"/file/"+ fileDownloadDTO.getFileName();
        return initOss().exportOssFile(Path,isPrivate);
    }

    @Override
    public String createTemplateUrlOfFile(String filePath,int expiresIn,boolean isPrivate) {
        return initOss().getTemplateUrl(filePath,expiresIn,isPrivate);
    }

    @Override
    public FileListVo queryFileList(String prefix, boolean recursive, boolean isPrivate) {
        List<OSSObjectSummary> ossObjectSummaries = initOss().queryFileListByPath(prefix,true);
        List<File> files= new ArrayList<>();
        for (OSSObjectSummary file : ossObjectSummaries) {
            String fNamePath=file.getKey();
            // 找到最后一个斜杠的位置
            int lastSlashIndex = fNamePath.lastIndexOf('/');
            // 使用substring截取从最后一个斜杠位置开始到字符串末尾的部分
            String fileName = fNamePath.substring(lastSlashIndex + 1);
            String pathPart = fNamePath.substring(0, lastSlashIndex);
            File f = new File();
            f.setEtag(file.getETag());
            f.setFileName(fileName);
            f.setFileType(file.getType());
            f.setFileSize(String.valueOf(file.getSize()));
            f.setLastModified(String.valueOf(file.getLastModified()));
            f.setStorageClass(file.getStorageClass());
            f.setFilePath(pathPart);

            files.add(f);
        }
        FileListVo fileListVo = new FileListVo();
        fileListVo.setFiles(files);
        return fileListVo;
    }

    @Override
    public InputStream BreakPointDownload(String path, long offset, long length, boolean isPrivate) {
        return initOss().getObject(path,offset,length,isPrivate);
    }


}
