package com.titancore.framework.common.translation.annotation;

import com.titancore.framework.common.translation.enums.TranslationType;

import java.lang.annotation.*;

/**
 * 翻译注解
 * 用于标记需要翻译的字段
 *
 * @author TitanCore
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Translation {

    /**
     * 翻译类型
     */
    TranslationType type() default TranslationType.AUTO;

    /**
     * 翻译源字段名
     * 为空时默认使用当前字段名（去掉可能的 _text / _name 后缀）
     */
    String source() default "";

    /**
     * 字典类型编码（仅 type = DICTIONARY 时使用）
     */
    String dictType() default "";

    /**
     * 枚举类（仅 type = ENUM 时使用）
     */
    Class<?> enumClass() default Void.class;

    /**
     * 翻译方法名（仅 type = METHOD 时使用）
     * 格式：类全路径#方法名 或 Spring Bean 名称.方法名
     * 方法签名要求：Object methodName(Object sourceValue)
     */
    String method() default "";

    /**
     * 其他扩展参数
     */
    String[] params() default {};
}
