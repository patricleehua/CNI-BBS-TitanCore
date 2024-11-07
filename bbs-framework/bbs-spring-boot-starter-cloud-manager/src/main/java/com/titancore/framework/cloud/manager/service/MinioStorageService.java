package com.titancore.framework.cloud.manager.service;


import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.cloud.manager.domain.entity.File;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;
import com.titancore.framework.cloud.manager.properties.CloudProperties;
import com.titancore.framework.cloud.manager.urils.MinioUtil;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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
        return initOss().uploadFile(file,filePath,isPrivate);
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
        return initOss().uploadFile(file, folderName,isPrivate);
    }

    @Override
    public boolean deleteByPath(FileDelDTO fileDelDTO,boolean isPrivate) {
        String path = fileDelDTO.getUserId() + "/" + fileDelDTO.getPath();
        if (fileDelDTO.getFileName() != null && !fileDelDTO.getFileName().isEmpty() ) {
            path = fileDelDTO.getPath().endsWith("/") ? path : path + "/";
            // 删除单个文件
            return initOss().deleteFile(path + fileDelDTO.getFileName(),isPrivate);
        } else {
            // 删除整个目录
            return initOss().deleteDirectory(path,isPrivate);
        }
    }
    @Override
    public FileListVo queryFileList(String prefix, boolean recursive, boolean isPrivate) {
        List<Item> items = initOss().queryFileListByPath(prefix, recursive ,isPrivate);
        List<File> files= new ArrayList<>();
        for (Item item : items) {
            String fNamePath=item.objectName();
            // 找到最后一个斜杠的位置
            int lastSlashIndex = fNamePath.lastIndexOf('/');
            // 使用substring截取从最后一个斜杠位置开始到字符串末尾的部分
            String result = fNamePath.substring(lastSlashIndex + 1);
            File f = new File();
            f.setEtag(item.etag());
            f.setFileName(result);
            String fileType = item.userMetadata() != null ? item.userMetadata().get("Content-Type") : null;
            f.setFileType(fileType);
            f.setFileSize(String.valueOf(item.size()));

            f.setStorageClass(item.storageClass());
            files.add(f);
        }
        FileListVo fileListVo = new FileListVo();
        fileListVo.setFiles(files);
        return fileListVo;
    }

}
