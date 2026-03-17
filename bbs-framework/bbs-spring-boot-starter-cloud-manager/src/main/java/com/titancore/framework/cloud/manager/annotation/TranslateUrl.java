package com.titancore.framework.cloud.manager.annotation;

import com.titancore.framework.cloud.manager.url.UrlType;

import java.lang.annotation.*;

/**
 * URL 翻译注解
 * 用于标记需要翻译的云存储 URL 字段
 *
 * @author TitanCore
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface TranslateUrl {

    /**
     * 目标 URL 类型
     */
    UrlType type() default UrlType.CDN;

    /**
     * 源字段名
     * 为空时默认为当前字段
     */
    String source() default "";

    /**
     * 额外参数
     * 用于缩略图、水印等功能
     */
    String[] params() default {};

    /**
     * 是否批量翻译（字段为 List<String> 时使用）
     */
    boolean isList() default false;
}
