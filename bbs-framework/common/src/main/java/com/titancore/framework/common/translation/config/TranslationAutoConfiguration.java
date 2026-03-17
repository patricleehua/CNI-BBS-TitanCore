package com.titancore.framework.common.translation.config;

import com.titancore.framework.common.translation.aspect.TranslationAspect;
import com.titancore.framework.common.translation.handler.DictionaryTranslationHandler;
import com.titancore.framework.common.translation.handler.EnumTranslationHandler;
import com.titancore.framework.common.translation.handler.MethodTranslationHandler;
import com.titancore.framework.common.translation.manager.TranslationManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 翻译功能自动配置类
 *
 * @author TitanCore
 */
@Configuration
public class TranslationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TranslationAspect translationAspect() {
        return new TranslationAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public TranslationManager translationManager() {
        return new TranslationManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public DictionaryTranslationHandler dictionaryTranslationHandler() {
        return new DictionaryTranslationHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public EnumTranslationHandler enumTranslationHandler() {
        return new EnumTranslationHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public MethodTranslationHandler methodTranslationHandler() {
        return new MethodTranslationHandler();
    }
}
