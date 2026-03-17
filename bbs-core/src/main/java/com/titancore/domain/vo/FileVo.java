package com.titancore.domain.vo;

import com.titancore.framework.cloud.manager.annotation.TranslateUrl;
import com.titancore.framework.cloud.manager.url.UrlType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件 VO - 展示 URL 翻译注解的使用
 *
 * @author TitanCore
 */
@Data
public class FileVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileId;
    private String fileName;

    /**
     * 原始存储 URL（阿里云 OSS 或 MinIO）
     * 例如：https://bucket.endpoint.com/path/to/file.jpg
     */
    private String originalUrl;

    /**
     * CDN 加速 URL
     * 通过 @TranslateUrl 注解自动将 originalUrl 转换为 CDN URL
     */
    @TranslateUrl(type = UrlType.CDN, source = "originalUrl")
    private String cdnUrl;

    /**
     * 缩略图 URL（宽度 200，高度 200）
     * 仅适用于图片，自动添加缩略图处理参数
     */
    @TranslateUrl(type = UrlType.THUMBNAIL, source = "originalUrl", params = {"200", "200"})
    private String thumbnailUrl;

    /**
     * 内部路径（仅路径部分，无域名）
     * 例如：path/to/file.jpg
     */
    @TranslateUrl(type = UrlType.INTERNAL_PATH, source = "originalUrl")
    private String internalPath;

    private String fileType;
    private Long fileSize;
    private String createTime;
}
