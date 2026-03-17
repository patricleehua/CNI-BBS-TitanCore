package com.titancore.framework.common.translation.aspect;

import com.titancore.framework.common.translation.annotation.Translation;
import com.titancore.framework.common.translation.annotation.TranslationIgnore;
import com.titancore.framework.common.translation.enums.TranslationType;
import com.titancore.framework.common.translation.manager.TranslationManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 翻译切面
 * 处理标记了 @Translation 注解的字段的自动翻译
 *
 * @author TitanCore
 */
@Aspect
@Component
@Slf4j
@Order(100)
public class TranslationAspect {

    @Autowired
    private TranslationManager translationManager;

    /**
     * 切点：所有 Controller 方法
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || " +
              "@within(org.springframework.stereotype.Controller)")
    public void controllerPointcut() {}

    /**
     * 环绕通知：处理翻译
     */
    @Around("controllerPointcut()")
    public Object doTranslate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result != null) {
            translate(result);
        }

        return result;
    }

    /**
     * 递归翻译对象
     *
     * @param obj 要翻译的对象
     */
    public void translate(Object obj) {
        if (obj == null) {
            return;
        }

        // 处理集合类型
        if (obj instanceof Collection) {
            for (Object item : (Collection<?>) obj) {
                translate(item);
            }
            return;
        }

        // 处理数组类型
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            for (Object item : array) {
                translate(item);
            }
            return;
        }

        // 处理 Map 类型（只处理值）
        if (obj instanceof Map) {
            for (Object value : ((Map<?, ?>) obj).values()) {
                translate(value);
            }
            return;
        }

        // 处理基本类型和包装类型
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

        // 检查类是否标记了忽略翻译
        if (clazz.isAnnotationPresent(TranslationIgnore.class)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 跳过静态字段
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // 跳过标记了 @TranslationIgnore 的字段
            if (field.isAnnotationPresent(TranslationIgnore.class)) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object fieldValue = field.get(obj);

                // 如果字段值是对象类型，递归翻译
                if (fieldValue != null && !isBasicType(fieldValue.getClass())) {
                    translate(fieldValue);
                }

                // 处理 @Translation 注解
                Translation translation = field.getAnnotation(Translation.class);
                if (translation != null) {
                    handleTranslation(obj, field, translation);
                }

            } catch (Exception e) {
                log.warn("翻译字段失败：{}.{}", clazz.getSimpleName(), field.getName(), e);
            }
        }
    }

    /**
     * 处理单个字段的翻译
     *
     * @param obj         对象
     * @param field       字段
     * @param translation 翻译注解
     */
    private void handleTranslation(Object obj, Field field, Translation translation) throws IllegalAccessException {
        String sourceFieldName = translation.source();
        TranslationType type = translation.type();

        // 自动推断源字段
        if (!sourceFieldName.isEmpty()) {
            // 指定了源字段
            Field sourceField = findField(obj.getClass(), sourceFieldName);
            if (sourceField != null) {
                sourceField.setAccessible(true);
                Object sourceValue = sourceField.get(obj);
                Object translatedValue = translationManager.translate(sourceValue, translation, field.getType());
                if (translatedValue != null) {
                    field.set(obj, translatedValue);
                }
            }
        } else if (type == TranslationType.AUTO) {
            // 自动模式：尝试根据字段名推断源字段
            String fieldName = field.getName();
            String inferredSourceField = inferSourceField(fieldName);

            if (inferredSourceField != null) {
                Field sourceField = findField(obj.getClass(), inferredSourceField);
                if (sourceField != null) {
                    sourceField.setAccessible(true);
                    Object sourceValue = sourceField.get(obj);
                    Object translatedValue = translationManager.translate(sourceValue, translation, field.getType());
                    if (translatedValue != null) {
                        field.set(obj, translatedValue);
                    }
                }
            }
        } else {
            // 其他类型：直接使用当前字段的值作为源值
            Object sourceValue = field.get(obj);
            Object translatedValue = translationManager.translate(sourceValue, translation, field.getType());
            if (translatedValue != null) {
                field.set(obj, translatedValue);
            }
        }
    }

    /**
     * 根据目标字段名推断源字段名
     * 例如：statusName -> status, statusText -> status
     *
     * @param targetFieldName 目标字段名
     * @return 推断的源字段名
     */
    private String inferSourceField(String targetFieldName) {
        // 常见的翻译字段后缀
        String[] suffixes = {"Name", "Text", "Desc", "Description", "Label", "Title"};

        for (String suffix : suffixes) {
            if (targetFieldName.endsWith(suffix) && targetFieldName.length() > suffix.length()) {
                String sourceField = targetFieldName.substring(0, targetFieldName.length() - suffix.length());
                // 首字母小写
                return Character.toLowerCase(sourceField.charAt(0)) + sourceField.substring(1);
            }
        }

        return null;
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
