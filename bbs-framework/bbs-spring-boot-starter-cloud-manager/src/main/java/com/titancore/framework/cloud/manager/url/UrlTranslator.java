package com.titancore.framework.cloud.manager.url;

/**
 * URL 翻译器接口
 * 用于将云存储 URL 转换为不同格式
 *
 * @author TitanCore
 */
public interface UrlTranslator {

    /**
     * 是否支持该 URL
     *
     * @param url 原始 URL
     * @return 是否支持
     */
    boolean supports(String url);

    /**
     * 翻译 URL
     *
     * @param url    原始 URL
     * @param type   目标类型
     * @param params 额外参数
     * @return 翻译后的 URL
     */
    String translate(String url, UrlType type, Object... params);

    /**
     * 获取翻译器优先级，数值越小优先级越高
     *
     * @return 优先级
     */
    default int getOrder() {
        return 0;
    }
}
