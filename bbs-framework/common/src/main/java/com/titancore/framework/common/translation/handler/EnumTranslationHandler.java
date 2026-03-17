package com.titancore.framework.common.translation.handler;

import com.titancore.framework.common.translation.annotation.Translation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 枚举翻译处理器
 * 支持枚举值翻译为中文/描述
 *
 * @author TitanCore
 */
@Slf4j
public class EnumTranslationHandler implements TranslationHandler {

    /**
     * 枚举描述方法缓存
     */
    private static final Map<Class<?>, Method> ENUM_DESC_METHOD_CACHE = new ConcurrentHashMap<>();

    @Override
    public boolean supports(String type) {
        return "enum".equals(type);
    }

    @Override
    public Object translate(Object sourceValue, Translation translation, Class<?> targetFieldType) {
        if (sourceValue == null) {
            return null;
        }

        Class<?> enumClass = translation.enumClass();
        if (enumClass == Void.class) {
            // 尝试自动推断枚举类
            enumClass = inferEnumClass(sourceValue);
        }

        if (enumClass == null || !enumClass.isEnum()) {
            log.warn("枚举翻译失败：无效的枚举类 [{}]", enumClass);
            return null;
        }

        return translateEnum(sourceValue, enumClass);
    }

    /**
     * 翻译枚举值
     *
     * @param sourceValue 源值
     * @param enumClass   枚举类
     * @return 翻译后的值
     */
    @SuppressWarnings("unchecked")
    private Object translateEnum(Object sourceValue, Class<?> enumClass) {
        try {
            Object[] enumConstants = enumClass.getEnumConstants();

            // 1. 尝试直接匹配枚举名称
            if (sourceValue instanceof String) {
                String sourceStr = (String) sourceValue;
                for (Object enumConstant : enumConstants) {
                    if (((Enum<?>) enumConstant).name().equals(sourceStr)) {
                        return getEnumDescription(enumConstant);
                    }
                }
            }

            // 2. 尝试匹配枚举的 getValue() 方法（MyBatis-Plus IEnum 风格）
            for (Object enumConstant : enumConstants) {
                Object value = getEnumValue(enumConstant);
                if (value != null && value.equals(sourceValue)) {
                    return getEnumDescription(enumConstant);
                }
            }

            // 3. 尝试匹配枚举的 ordinal
            if (sourceValue instanceof Number) {
                int ordinal = ((Number) sourceValue).intValue();
                if (ordinal >= 0 && ordinal < enumConstants.length) {
                    return getEnumDescription(enumConstants[ordinal]);
                }
            }

        } catch (Exception e) {
            log.error("枚举翻译失败", e);
        }

        return null;
    }

    /**
     * 获取枚举描述
     * 优先级：getDesc() > getDescription() > getLabel() > toString() > name()
     *
     * @param enumConstant 枚举常量
     * @return 描述
     */
    private Object getEnumDescription(Object enumConstant) {
        Class<?> enumClass = enumConstant.getClass();

        // 尝试获取缓存的方法
        Method method = ENUM_DESC_METHOD_CACHE.get(enumClass);
        if (method != null) {
            try {
                return method.invoke(enumConstant);
            } catch (Exception ignored) {
            }
        }

        // 尝试各种描述方法
        String[] methodNames = {"getDesc", "getDescription", "getLabel", "getText", "getName", "desc", "description"};
        for (String methodName : methodNames) {
            try {
                Method m = enumClass.getMethod(methodName);
                Object result = m.invoke(enumConstant);
                if (result != null) {
                    ENUM_DESC_METHOD_CACHE.put(enumClass, m);
                    return result;
                }
            } catch (Exception ignored) {
            }
        }

        // 默认返回枚举名称
        return ((Enum<?>) enumConstant).name();
    }

    /**
     * 获取枚举值（用于匹配）
     *
     * @param enumConstant 枚举常量
     * @return 枚举值
     */
    private Object getEnumValue(Object enumConstant) {
        Class<?> enumClass = enumConstant.getClass();

        // 尝试 getValue() 方法（MyBatis-Plus IEnum 风格）
        try {
            Method method = enumClass.getMethod("getValue");
            return method.invoke(enumConstant);
        } catch (Exception ignored) {
        }

        // 尝试 value 字段
        try {
            return enumClass.getField("value").get(enumConstant);
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 推断枚举类
     *
     * @param sourceValue 源值
     * @return 枚举类
     */
    private Class<?> inferEnumClass(Object sourceValue) {
        // 这里可以根据项目需求扩展，例如通过字段名推断枚举类型
        return null;
    }
}
