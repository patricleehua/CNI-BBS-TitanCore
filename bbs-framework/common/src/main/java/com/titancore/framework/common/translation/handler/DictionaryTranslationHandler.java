package com.titancore.framework.common.translation.handler;

import com.titancore.framework.common.translation.annotation.Translation;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典翻译处理器
 * 支持内存中的字典数据翻译
 *
 * @author TitanCore
 */
@Slf4j
public class DictionaryTranslationHandler implements TranslationHandler {

    /**
     * 字典缓存 Map<字典类型, Map<字典编码, 字典名称>>
     */
    private static final Map<String, Map<Object, String>> DICT_CACHE = new ConcurrentHashMap<>();

    @Override
    public boolean supports(String type) {
        return "dictionary".equals(type);
    }

    @Override
    public Object translate(Object sourceValue, Translation translation, Class<?> targetFieldType) {
        if (sourceValue == null) {
            return null;
        }

        String dictType = translation.dictType();
        if (dictType.isEmpty()) {
            log.warn("字典翻译失败：未指定字典类型");
            return null;
        }

        Map<Object, String> dictMap = DICT_CACHE.get(dictType);
        if (dictMap == null) {
            log.warn("字典翻译失败：未找到字典类型 [{}]", dictType);
            return null;
        }

        return dictMap.get(sourceValue);
    }

    /**
     * 注册字典数据
     *
     * @param dictType 字典类型
     * @param dictMap  字典映射
     */
    public static void registerDictionary(String dictType, Map<Object, String> dictMap) {
        DICT_CACHE.put(dictType, new ConcurrentHashMap<>(dictMap));
    }

    /**
     * 注册单个字典项
     *
     * @param dictType 字典类型
     * @param code     字典编码
     * @param value    字典值
     */
    public static void registerDictItem(String dictType, Object code, String value) {
        DICT_CACHE.computeIfAbsent(dictType, k -> new ConcurrentHashMap<>()).put(code, value);
    }

    /**
     * 清空字典缓存
     *
     * @param dictType 字典类型
     */
    public static void clearDictionary(String dictType) {
        DICT_CACHE.remove(dictType);
    }

    /**
     * 清空所有字典缓存
     */
    public static void clearAll() {
        DICT_CACHE.clear();
    }
}
