package com.titancore.framework.common.translation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 翻译类型枚举
 *
 * @author TitanCore
 */
@Getter
@AllArgsConstructor
public enum TranslationType {

    /**
     * 自动识别
     * 根据字段名自动推断翻译类型
     */
    AUTO("auto", "自动识别"),

    /**
     * 字典翻译
     * 根据字典类型和编码翻译
     */
    DICTIONARY("dictionary", "字典翻译"),

    /**
     * 枚举翻译
     * 根据枚举类翻译
     */
    ENUM("enum", "枚举翻译"),

    /**
     * 方法翻译
     * 通过指定方法进行翻译
     */
    METHOD("method", "方法翻译"),

    /**
     * 自定义翻译
     * 通过自定义翻译器处理
     */
    CUSTOM("custom", "自定义翻译");

    private final String code;
    private final String desc;
}
