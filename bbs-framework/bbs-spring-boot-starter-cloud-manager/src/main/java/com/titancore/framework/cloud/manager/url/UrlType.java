package com.titancore.framework.cloud.manager.url;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * URL 类型枚举
 *
 * @author TitanCore
 */
@Getter
@AllArgsConstructor
public enum UrlType {

    /**
     * 原始 URL
     */
    ORIGINAL("original", "原始 URL"),

    /**
     * 完整访问 URL
     */
    FULL("full", "完整 URL"),

    /**
     * CDN URL
     */
    CDN("cdn", "CDN 加速 URL"),

    /**
     * 内部路径（仅路径部分）
     */
    INTERNAL_PATH("internal", "内部路径"),

    /**
     * 临时访问 URL（带过期时间）
     */
    TEMPORARY("temporary", "临时 URL"),

    /**
     * 缩略图 URL
     */
    THUMBNAIL("thumbnail", "缩略图 URL"),

    /**
     * 水印 URL
     */
    WATERMARK("watermark", "水印 URL"),

    /**
     * 自定义格式
     */
    CUSTOM("custom", "自定义格式");

    private final String code;
    private final String desc;

    public static UrlType fromCode(String code) {
        for (UrlType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return ORIGINAL;
    }
}
