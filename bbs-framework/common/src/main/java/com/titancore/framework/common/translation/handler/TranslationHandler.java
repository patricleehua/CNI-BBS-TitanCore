package com.titancore.framework.common.translation.handler;

import com.titancore.framework.common.translation.annotation.Translation;

/**
 * 翻译处理器接口
 * 实现此接口以提供自定义翻译逻辑
 *
 * @author TitanCore
 */
public interface TranslationHandler {

    /**
     * 是否支持该翻译类型
     *
     * @param type 翻译类型
     * @return 是否支持
     */
    boolean supports(String type);

    /**
     * 执行翻译
     *
     * @param sourceValue 源值
     * @param translation 翻译注解
     * @param targetFieldType 目标字段类型
     * @return 翻译后的值
     */
    Object translate(Object sourceValue, Translation translation, Class<?> targetFieldType);

    /**
     * 获取处理器优先级，数值越小优先级越高
     *
     * @return 优先级
     */
    default int getOrder() {
        return 0;
    }
}
