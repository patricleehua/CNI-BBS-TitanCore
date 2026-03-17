package com.titancore.framework.common.translation.manager;

import com.titancore.framework.common.translation.annotation.Translation;
import com.titancore.framework.common.translation.enums.TranslationType;
import com.titancore.framework.common.translation.handler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 翻译管理器
 * 管理所有翻译处理器并协调翻译过程
 *
 * @author TitanCore
 */
@Slf4j
@Component
public class TranslationManager {

    @Autowired
    private List<TranslationHandler> handlers = new ArrayList<>();

    private final Map<String, TranslationHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 注册默认处理器
        registerDefaultHandlers();

        // 按优先级排序
        handlers.sort(Comparator.comparingInt(TranslationHandler::getOrder));

        // 构建处理器映射
        for (TranslationHandler handler : handlers) {
            for (TranslationType type : TranslationType.values()) {
                if (handler.supports(type.getCode())) {
                    handlerMap.put(type.getCode(), handler);
                }
            }
        }

        log.info("翻译管理器初始化完成，共注册 {} 个处理器", handlers.size());
    }

    /**
     * 注册默认处理器
     */
    private void registerDefaultHandlers() {
        // 检查是否已存在，避免重复注册
        boolean hasDict = handlers.stream().anyMatch(h -> h instanceof DictionaryTranslationHandler);
        boolean hasEnum = handlers.stream().anyMatch(h -> h instanceof EnumTranslationHandler);
        boolean hasMethod = handlers.stream().anyMatch(h -> h instanceof MethodTranslationHandler);

        if (!hasDict) {
            handlers.add(new DictionaryTranslationHandler());
        }
        if (!hasEnum) {
            handlers.add(new EnumTranslationHandler());
        }
        if (!hasMethod) {
            handlers.add(new MethodTranslationHandler());
        }
    }

    /**
     * 执行翻译
     *
     * @param sourceValue     源值
     * @param translation     翻译注解
     * @param targetFieldType 目标字段类型
     * @return 翻译后的值
     */
    public Object translate(Object sourceValue, Translation translation, Class<?> targetFieldType) {
        if (sourceValue == null) {
            return null;
        }

        TranslationType type = translation.type();
        TranslationHandler handler = handlerMap.get(type.getCode());

        if (handler == null) {
            log.warn("未找到翻译处理器：{}", type.getCode());
            return null;
        }

        try {
            return handler.translate(sourceValue, translation, targetFieldType);
        } catch (Exception e) {
            log.error("翻译失败：类型={}, 源值={}", type, sourceValue, e);
            return null;
        }
    }

    /**
     * 注册自定义翻译处理器
     *
     * @param handler 处理器
     */
    public void registerHandler(TranslationHandler handler) {
        handlers.add(handler);
        handlers.sort(Comparator.comparingInt(TranslationHandler::getOrder));

        for (TranslationType type : TranslationType.values()) {
            if (handler.supports(type.getCode())) {
                handlerMap.put(type.getCode(), handler);
            }
        }
    }
}
