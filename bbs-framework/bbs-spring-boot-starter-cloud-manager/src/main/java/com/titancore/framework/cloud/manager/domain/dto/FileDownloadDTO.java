package com.titancore.framework.cloud.manager.domain.dto;

import lombok.Data;

/**
 * 文件下载DTO/获取临时文件
 */
@Data
public class FileDownloadDTO {
    private String fileName;
    private String userId;
    private int expiresIn;
    /**
     * 是否私有，私有0/公开1
     */
    private String isPrivate;
}
