package com.titancore.framework.cloud.manager.aspect;

import com.titancore.framework.cloud.manager.annotation.TranslateUrl;
import com.titancore.framework.cloud.manager.url.UrlTranslationService;
import com.titancore.framework.cloud.manager.url.UrlType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * URL 翻译切面
 * 自动处理标记了 @TranslateUrl 注解的字段
 *
 * @author TitanCore
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Order(50)
public class UrlTranslationAspect {

    private final UrlTranslationService urlTranslationService;

    /**
     * 切点：所有 Controller 方法
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || " +
              "@within(org.springframework.stereotype.Controller)")
    public void controllerPointcut() {}

    /**
     * 环绕通知：处理 URL 翻译
     */
    @Around("controllerPointcut()")
    public Object doTranslate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result != null) {
            translateUrls(result);
        }

        return result;
    }

    /**
     * 递归翻译对象中的 URL 字段
     *
     * @param obj 要翻译的对象
     */
    public void translateUrls(Object obj) {
        if (obj == null) {
            return;
        }

        // 处理集合类型
        if (obj instanceof Collection) {
            for (Object item : (Collection<?>) obj) {
                translateUrls(item);
            }
            return;
        }

        // 处理数组类型
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            for (Object item : array) {
                translateUrls(item);
            }
            return;
        }

        // 处理基本类型
        if (isBasicType(obj.getClass())) {
            return;
        }

        // 处理普通对象
        translateObject(obj);
    }

    /**
     * 翻译单个对象
     *
     * @param obj 要翻译的对象
     */
    private void translateObject(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 跳过静态字段
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object fieldValue = field.get(obj);

                if (fieldValue == null) {
                    continue;
                }

                // 如果字段值是对象类型，递归翻译
                if (!isBasicType(fieldValue.getClass())) {
                    translateUrls(fieldValue);
                }

                // 处理 @TranslateUrl 注解
                TranslateUrl translateUrl = field.getAnnotation(TranslateUrl.class);
                if (translateUrl != null) {
                    handleUrlTranslation(obj, field, translateUrl, fieldValue);
                }

            } catch (Exception e) {
                log.warn("翻译 URL 字段失败：{}.{}", clazz.getSimpleName(), field.getName(), e);
            }
        }
    }

    /**
     * 处理 URL 翻译
     *
     * @param obj        对象
     * @param field      字段
     * @param annotation 注解
     * @param value      字段值
     */
    private void handleUrlTranslation(Object obj, Field field, TranslateUrl annotation, Object value) throws IllegalAccessException {
        UrlType type = annotation.type();
        boolean isList = annotation.isList();

        // 获取源值
        Object sourceValue;
        String sourceFieldName = annotation.source();

        if (StringUtils.hasText(sourceFieldName)) {
            // 从指定源字段获取
            Field sourceField = findField(obj.getClass(), sourceFieldName);
            if (sourceField != null) {
                sourceField.setAccessible(true);
                sourceValue = sourceField.get(obj);
            } else {
                log.warn("未找到源字段：{}.{}", obj.getClass().getSimpleName(), sourceFieldName);
                return;
            }
        } else {
            sourceValue = value;
        }

        if (sourceValue == null) {
            return;
        }

        // 解析额外参数
        Object[] params = parseParams(annotation.params());

        // 执行翻译
        if (isList && sourceValue instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> urls = (List<String>) sourceValue;
            List<String> translatedUrls = urlTranslationService.translateList(urls, type);
            field.set(obj, translatedUrls);
        } else if (sourceValue instanceof String) {
            String url = (String) sourceValue;
            String translatedUrl = urlTranslationService.translate(url, type, params);
            field.set(obj, translatedUrl);
        }
    }

    /**
     * 解析参数
     *
     * @param params 参数字符串数组
     * @return 解析后的参数
     */
    private Object[] parseParams(String[] params) {
        if (params == null || params.length == 0) {
            return new Object[0];
        }

        Object[] result = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            // 尝试解析为整数
            try {
                result[i] = Integer.parseInt(param);
                continue;
            } catch (NumberFormatException ignored) {
            }
            // 尝试解析为布尔值
            if ("true".equalsIgnoreCase(param) || "false".equalsIgnoreCase(param)) {
                result[i] = Boolean.parseBoolean(param);
                continue;
            }
            // 作为字符串
            result[i] = param;
        }
        return result;
    }

    /**
     * 在类及其父类中查找字段
     *
     * @param clazz     类
     * @param fieldName 字段名
     * @return 字段
     */
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 判断是否为基本类型或包装类型
     *
     * @param clazz 类
     * @return 是否为基本类型
     */
    private boolean isBasicType(Class<?> clazz) {
        return clazz.isPrimitive() ||
               clazz == String.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Double.class ||
               clazz == Float.class ||
               clazz == Boolean.class ||
               clazz == Byte.class ||
               clazz == Short.class ||
               clazz == Character.class ||
               Number.class.isAssignableFrom(clazz) ||
               clazz.isEnum() ||
               clazz.getName().startsWith("java.time.");
    }
}
