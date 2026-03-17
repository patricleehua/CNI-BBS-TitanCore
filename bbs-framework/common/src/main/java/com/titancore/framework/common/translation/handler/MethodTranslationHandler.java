package com.titancore.framework.common.translation.handler;

import com.titancore.framework.common.translation.annotation.Translation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法翻译处理器
 * 通过指定的方法进行翻译
 *
 * @author TitanCore
 */
@Slf4j
public class MethodTranslationHandler implements TranslationHandler, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 方法缓存
     */
    private static final Map<String, MethodWrapper> METHOD_CACHE = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean supports(String type) {
        return "method".equals(type);
    }

    @Override
    public Object translate(Object sourceValue, Translation translation, Class<?> targetFieldType) {
        String methodStr = translation.method();
        if (!StringUtils.hasText(methodStr)) {
            log.warn("方法翻译失败：未指定方法");
            return null;
        }

        try {
            MethodWrapper methodWrapper = getMethodWrapper(methodStr);
            if (methodWrapper == null) {
                return null;
            }

            return methodWrapper.invoke(sourceValue);
        } catch (Exception e) {
            log.error("方法翻译失败：{}，源值：{}" , methodStr, sourceValue, e);
            return null;
        }
    }

    /**
     * 获取方法包装器
     *
     * @param methodStr 方法字符串（格式：类全路径#方法名 或 beanName.methodName）
     * @return 方法包装器
     */
    private MethodWrapper getMethodWrapper(String methodStr) {
        // 先尝试从缓存获取
        MethodWrapper cached = METHOD_CACHE.get(methodStr);
        if (cached != null) {
            return cached;
        }

        // 解析方法字符串
        MethodWrapper wrapper = parseMethod(methodStr);
        if (wrapper != null) {
            METHOD_CACHE.put(methodStr, wrapper);
        }
        return wrapper;
    }

    /**
     * 解析方法
     *
     * @param methodStr 方法字符串
     * @return 方法包装器
     */
    private MethodWrapper parseMethod(String methodStr) {
        try {
            // 格式1：类全路径#方法名
            if (methodStr.contains("#")) {
                String[] parts = methodStr.split("#");
                String className = parts[0];
                String methodName = parts[1];

                Class<?> clazz = Class.forName(className);
                Method method = findMethod(clazz, methodName);
                return new MethodWrapper(null, method, null);
            }

            // 格式2：beanName.methodName
            if (methodStr.contains(".")) {
                String[] parts = methodStr.split("\\.");
                String beanName = parts[0];
                String methodName = parts[1];

                Object bean = applicationContext != null ? applicationContext.getBean(beanName) : null;
                if (bean == null) {
                    log.warn("方法翻译失败：未找到 Bean [{}]", beanName);
                    return null;
                }

                Method method = findMethod(bean.getClass(), methodName);
                return new MethodWrapper(bean, method, null);
            }

            log.warn("方法翻译失败：无效的方法格式 [{}]", methodStr);
            return null;

        } catch (Exception e) {
            log.error("解析翻译方法失败：{}" , methodStr, e);
            return null;
        }
    }

    /**
     * 查找方法
     *
     * @param clazz      类
     * @param methodName 方法名
     * @return 方法
     */
    private Method findMethod(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    /**
     * 方法包装器
     */
    private record MethodWrapper(Object target, Method method, Class<?> paramType) {
        MethodWrapper {
            if (method != null && method.getParameterCount() > 0) {
                paramType = method.getParameterTypes()[0];
            }
        }

        Object invoke(Object param) throws Exception {
            if (method == null) {
                return null;
            }
            return method.invoke(target, param);
        }
    }
}
