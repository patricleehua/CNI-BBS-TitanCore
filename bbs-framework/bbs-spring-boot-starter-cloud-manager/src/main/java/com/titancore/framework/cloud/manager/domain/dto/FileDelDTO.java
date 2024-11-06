package com.titancore.framework.cloud.manager.domain.dto;

import lombok.Data;

@Data
public class FileDelDTO {
    /**
     * 例 1/article/background/176XXX/a.jpg
     */

    /**
     * 文件名 a.jpg
     */
    private String fileName;
    /**
     * 用户id 例: 1
     */
    private String userId;
    /**
     * 路径 例: post/background/176XXX/
     */
    private String path;
    /**
     * 是否为私有文件
     */
    private boolean isPrivate = false;

}
