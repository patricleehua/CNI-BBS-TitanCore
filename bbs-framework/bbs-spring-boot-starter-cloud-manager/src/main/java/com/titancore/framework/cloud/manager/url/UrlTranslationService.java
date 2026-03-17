package com.titancore.framework.cloud.manager.url;

import com.titancore.framework.cloud.manager.properties.CloudProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * URL 翻译服务
 *
 * @author TitanCore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlTranslationService {

    private final CloudProperties cloudProperties;
    private final List<UrlTranslator> translators = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 注册默认翻译器
        translators.add(new AliyunOssUrlTranslator(cloudProperties));
        translators.add(new MinioUrlTranslator(cloudProperties));

        // 按优先级排序
        translators.sort(Comparator.comparingInt(UrlTranslator::getOrder));

        log.info("URL 翻译服务初始化完成，共注册 {} 个翻译器", translators.size());
    }

    /**
     * 翻译 URL
     *
     * @param url  原始 URL
     * @param type 目标类型
     * @return 翻译后的 URL
     */
    public String translate(String url, UrlType type) {
        return translate(url, type, new Object[0]);
    }

    /**
     * 翻译 URL
     *
     * @param url    原始 URL
     * @param type   目标类型
     * @param params 额外参数
     * @return 翻译后的 URL
     */
    public String translate(String url, UrlType type, Object... params) {
        if (!StringUtils.hasText(url)) {
            return url;
        }

        UrlTranslator translator = findTranslator(url);
        if (translator == null) {
            log.warn("未找到支持该 URL 的翻译器: {}", url);
            return url;
        }

        try {
            return translator.translate(url, type, params);
        } catch (Exception e) {
            log.error("URL 翻译失败: {}, 类型: {}", url, type, e);
            return url;
        }
    }

    /**
     * 批量翻译 URL
     *
     * @param urls URL 列表
     * @param type 目标类型
     * @return 翻译后的 URL 列表
     */
    public List<String> translateList(List<String> urls, UrlType type) {
        if (urls == null || urls.isEmpty()) {
            return urls;
        }

        List<String> result = new ArrayList<>();
        for (String url : urls) {
            result.add(translate(url, type));
        }
        return result;
    }

    /**
     * 转换为 CDN URL
     *
     * @param url 原始 URL
     * @return CDN URL
     */
    public String toCdnUrl(String url) {
        return translate(url, UrlType.CDN);
    }

    /**
     * 转换为缩略图 URL
     *
     * @param url    原始 URL
     * @param width  宽度
     * @param height 高度
     * @return 缩略图 URL
     */
    public String toThumbnailUrl(String url, int width, int height) {
        return translate(url, UrlType.THUMBNAIL, width, height);
    }

    /**
     * 转换为内部路径
     *
     * @param url 原始 URL
     * @return 内部路径
     */
    public String toInternalPath(String url) {
        return translate(url, UrlType.INTERNAL_PATH);
    }

    /**
     * 添加自定义翻译器
     *
     * @param translator 翻译器
     */
    public void addTranslator(UrlTranslator translator) {
        translators.add(translator);
        translators.sort(Comparator.comparingInt(UrlTranslator::getOrder));
    }

    /**
     * 查找支持的翻译器
     *
     * @param url URL
     * @return 翻译器
     */
    private UrlTranslator findTranslator(String url) {
        for (UrlTranslator translator : translators) {
            if (translator.supports(url)) {
                return translator;
            }
        }
        return null;
    }
}
