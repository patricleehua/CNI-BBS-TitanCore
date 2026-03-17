package com.titancore.framework.cloud.manager.url;

import com.titancore.framework.cloud.manager.properties.CloudProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 阿里云 OSS URL 翻译器
 *
 * @author TitanCore
 */
@Slf4j
public class AliyunOssUrlTranslator extends AbstractUrlTranslator {

    private static final String ALIYUN_OSS_DOMAIN = "aliyuncs.com";

    public AliyunOssUrlTranslator(CloudProperties cloudProperties) {
        super(cloudProperties);
    }

    @Override
    public boolean supports(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        return url.contains(ALIYUN_OSS_DOMAIN) ||
               url.contains(cloudProperties.getAliyun().getAoss().getEndpoint());
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
        // 如果已经是完整 URL，直接返回
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // 构建完整 URL
        String endpoint = cloudProperties.getAliyun().getAoss().getEndpoint();
        String bucketName = cloudProperties.getAliyun().getAoss().getBucketNameOpen();

        if (!endpoint.startsWith("http")) {
            endpoint = "https://" + endpoint;
        }

        // 去除开头的 /
        String path = url.startsWith("/") ? url.substring(1) : url;

        return endpoint + "/" + bucketName + "/" + path;
    }

    /**
     * 转换为 CDN URL
     */
    private String toCdnUrl(String url) {
        String cdnDomain = cloudProperties.getAliyun().getAoss().getCdnDomain();
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

        // 如果 URL 包含 endpoint，提取路径部分
        String endpoint = cloudProperties.getAliyun().getAoss().getEndpoint();
        if (url.contains(endpoint)) {
            int index = url.indexOf(endpoint);
            String afterEndpoint = url.substring(index + endpoint.length());
            // 去掉开头的 / 和 bucket 名
            String path = afterEndpoint.startsWith("/") ? afterEndpoint.substring(1) : afterEndpoint;
            String bucketName = cloudProperties.getAliyun().getAoss().getBucketNameOpen();
            if (path.startsWith(bucketName + "/")) {
                path = path.substring(bucketName.length() + 1);
            }
            return path;
        }

        // 如果是纯路径，直接返回
        if (!url.startsWith("http")) {
            return url.startsWith("/") ? url.substring(1) : url;
        }

        // 尝试提取路径
        return extractPath(url);
    }

    /**
     * 转换为缩略图 URL（使用阿里云 OSS 图片处理服务）
     */
    private String toThumbnailUrl(String url, Object... params) {
        if (!isImage(url)) {
            return url;
        }

        int width = params.length > 0 ? (int) params[0] : 200;
        int height = params.length > 1 ? (int) params[1] : 200;
        String mode = params.length > 2 ? (String) params[2] : "lfit";

        // 阿里云 OSS 图片处理参数
        String processParam = String.format("x-oss-process=image/resize,%s,w_%d,h_%d", mode, width, height);

        String separator = url.contains("?") ? "&" : "?";
        return url + separator + processParam;
    }

    /**
     * 转换为水印 URL
     */
    private String toWatermarkUrl(String url, Object... params) {
        if (!isImage(url)) {
            return url;
        }

        String watermarkText = params.length > 0 ? (String) params[0] : "TitanCore";
        int fontSize = params.length > 1 ? (int) params[1] : 30;
        String color = params.length > 2 ? (String) params[2] : "FFFFFF";

        // Base64 编码水印文字
        String base64Text = java.util.Base64.getEncoder().encodeToString(watermarkText.getBytes());

        String processParam = String.format(
            "x-oss-process=image/watermark,text_%s,size_%d,color_%s,g_se,x_10,y_10",
            base64Text, fontSize, color
        );

        String separator = url.contains("?") ? "&" : "?";
        return url + separator + processParam;
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
        return 10;
    }
}
