package com.titancore.framework.cloud.manager.config;

import com.titancore.framework.cloud.manager.aspect.UrlTranslationAspect;
import com.titancore.framework.cloud.manager.url.UrlTranslationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * URL 翻译自动配置类
 *
 * @author TitanCore
 */
@Configuration
public class UrlTranslationConfig {

    @Bean
    @ConditionalOnMissingBean
    public UrlTranslationService urlTranslationService(com.titancore.framework.cloud.manager.properties.CloudProperties cloudProperties) {
        return new UrlTranslationService(cloudProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public UrlTranslationAspect urlTranslationAspect(UrlTranslationService urlTranslationService) {
        return new UrlTranslationAspect(urlTranslationService);
    }
}
