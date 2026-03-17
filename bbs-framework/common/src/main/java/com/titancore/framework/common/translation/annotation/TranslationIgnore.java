package com.titancore.framework.common.translation.annotation;

import java.lang.annotation.*;

/**
 * 忽略翻译注解
 * 用于标记字段不参与翻译处理
 *
 * @author TitanCore
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface TranslationIgnore {
}
