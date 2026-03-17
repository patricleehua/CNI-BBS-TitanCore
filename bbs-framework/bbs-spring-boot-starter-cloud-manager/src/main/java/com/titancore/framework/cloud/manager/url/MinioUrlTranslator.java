package com.titancore.framework.cloud.manager.url;

import com.titancore.framework.cloud.manager.properties.CloudProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * MinIO URL 翻译器
 *
 * @author TitanCore
 */
@Slf4j
public class MinioUrlTranslator extends AbstractUrlTranslator {

    public MinioUrlTranslator(CloudProperties cloudProperties) {
        super(cloudProperties);
    }

    @Override
    public boolean supports(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        String endpoint = cloudProperties.getMinio().getEndpoint();
        return url.contains(endpoint) ||
               url.startsWith(cloudProperties.getMinio().getEndpoint());
    }

    @Override
    public String translate(String url, UrlType type, Object... params) {
        if (!StringUtils.hasText(url)) {
            return url;
        }

        return switch (type) {
            case ORIGINAL -> url;
            case FULL -> toFullUrl(url);
            case CDN -> toCdnUrl(url);
            case INTERNAL_PATH -> extractInternalPath(url);
            case THUMBNAIL -> toThumbnailUrl(url, params);
            case WATERMARK -> toWatermarkUrl(url, params);
            case CUSTOM -> toCustomUrl(url, params);
            default -> url;
        };
    }

    /**
     * 转换为完整 URL
     */
    private String toFullUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        String endpoint = cloudProperties.getMinio().getEndpoint();
        String bucketName = cloudProperties.getMinio().getBucketNameOpen();

        if (!endpoint.startsWith("http")) {
            endpoint = "https://" + endpoint;
        }

        String path = url.startsWith("/") ? url.substring(1) : url;
        return endpoint + "/" + bucketName + "/" + path;
    }

    /**
     * 转换为 CDN URL
     */
    private String toCdnUrl(String url) {
        String cdnDomain = cloudProperties.getMinio().getCdnDomain();
        if (!StringUtils.hasText(cdnDomain)) {
            log.warn("CDN 域名未配置，返回原始 URL");
            return url;
        }

        String path = extractInternalPath(url);
        if (!cdnDomain.startsWith("http")) {
            cdnDomain = "https://" + cdnDomain;
        }

        return cdnDomain + "/" + path;
    }

    /**
     * 提取内部路径
     */
    private String extractInternalPath(String url) {
        if (!StringUtils.hasText(url)) {
            return "";
        }

        String endpoint = cloudProperties.getMinio().getEndpoint();
        String bucketName = cloudProperties.getMinio().getBucketNameOpen();

        // 处理包含 endpoint 的 URL
        if (url.contains(endpoint)) {
            int index = url.indexOf(endpoint);
            String afterEndpoint = url.substring(index + endpoint.length());
            String path = afterEndpoint.startsWith("/") ? afterEndpoint.substring(1) : afterEndpoint;

            // 去掉 bucket 名
            if (path.startsWith(bucketName + "/")) {
                path = path.substring(bucketName.length() + 1);
            }
            return path;
        }

        // 处理纯路径
        if (!url.startsWith("http")) {
            return url.startsWith("/") ? url.substring(1) : url;
        }

        return extractPath(url);
    }

    /**
     * 转换为缩略图 URL（使用 MinIO 或外部图片处理服务）
     */
    private String toThumbnailUrl(String url, Object... params) {
        if (!isImage(url)) {
            return url;
        }

        int width = params.length > 0 ? (int) params[0] : 200;
        int height = params.length > 1 ? (int) params[1] : 200;

        // MinIO 可以通过自定义参数或配合图片处理服务实现
        // 这里使用简单的查询参数方式
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + String.format("width=%d&height=%d", width, height);
    }

    /**
     * 转换为水印 URL
     */
    private String toWatermarkUrl(String url, Object... params) {
        if (!isImage(url)) {
            return url;
        }

        String watermarkText = params.length > 0 ? (String) params[0] : "TitanCore";

        String separator = url.contains("?") ? "&" : "?";
        return url + separator + "watermark=" + watermarkText;
    }

    /**
     * 自定义 URL 格式
     */
    private String toCustomUrl(String url, Object... params) {
        if (params.length == 0) {
            return url;
        }

        String format = (String) params[0];
        String path = extractInternalPath(url);

        return format.replace("{path}", path)
                     .replace("{filename}", extractFileName(url))
                     .replace("{ext}", getExtension(url));
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
