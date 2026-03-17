package com.titancore.framework.common.translation.annotation;

import com.titancore.framework.common.translation.aspect.TranslationAspect;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用翻译功能注解
 * 在启动类或配置类上添加此注解以启用翻译功能
 *
 * @author TitanCore
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(TranslationAspect.class)
public @interface EnableTranslation {
}
