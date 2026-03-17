package com.titancore.framework.common.translation;

import com.titancore.framework.common.translation.aspect.TranslationAspect;
import com.titancore.framework.common.translation.handler.DictionaryTranslationHandler;
import com.titancore.framework.common.translation.handler.TranslationHandler;
import com.titancore.framework.common.translation.manager.TranslationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 翻译服务
 * 提供编程式翻译接口
 *
 * @author TitanCore
 */
@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationAspect translationAspect;
    private final TranslationManager translationManager;

    /**
     * 翻译对象
     *
     * @param obj 要翻译的对象
     */
    public void translate(Object obj) {
        translationAspect.translate(obj);
    }

    /**
     * 注册字典
     *
     * @param dictType 字典类型
     * @param dictMap  字典映射
     */
    public void registerDictionary(String dictType, Map<Object, String> dictMap) {
        DictionaryTranslationHandler.registerDictionary(dictType, dictMap);
    }

    /**
     * 注册字典项
     *
     * @param dictType 字典类型
     * @param code     字典编码
     * @param value    字典值
     */
    public void registerDictItem(String dictType, Object code, String value) {
        DictionaryTranslationHandler.registerDictItem(dictType, code, value);
    }

    /**
     * 注册自定义翻译处理器
     *
     * @param handler 处理器
     */
    public void registerHandler(TranslationHandler handler) {
        translationManager.registerHandler(handler);
    }

    /**
     * 清空字典缓存
     *
     * @param dictType 字典类型
     */
    public void clearDictionary(String dictType) {
        DictionaryTranslationHandler.clearDictionary(dictType);
    }
}
