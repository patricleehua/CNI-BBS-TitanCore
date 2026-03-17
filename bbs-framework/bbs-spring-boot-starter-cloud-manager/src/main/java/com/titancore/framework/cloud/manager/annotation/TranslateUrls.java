package com.titancore.framework.cloud.manager.annotation;

import java.lang.annotation.*;

/**
 * URL 翻译注解（批量）
 * 用于在类或方法上标记多个字段需要翻译
 *
 * @author TitanCore
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface TranslateUrls {

    /**
     * 需要翻译的字段配置
     */
    TranslateUrl[] value();
}
