package com.titancore.framework.cloud.manager.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File implements Serializable {

    /**
     * 文件的 ETag 值
     */
    private String etag;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型 (image/jpeg)
     */
    private String fileType;
    /**
     * 文件大小
     */
    private String fileSize;
    /**
     * 最后修改时间
     */
    private String lastModified;
    /**
     * 存储类型
     */
    private String storageClass;
    /**
     * 文件夹路径
     */
    private String filePath;

}
