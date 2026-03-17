package com.titancore.framework.cloud.manager.url;

import com.titancore.framework.cloud.manager.properties.CloudProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URL 翻译器抽象类
 *
 * @author TitanCore
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractUrlTranslator implements UrlTranslator {

    protected final CloudProperties cloudProperties;

    /**
     * 提取文件路径
     *
     * @param url 完整 URL
     * @return 文件路径
     */
    protected String extractPath(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();
            // 去掉开头的 /
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (MalformedURLException e) {
            // 如果不是完整 URL，直接返回
            return url;
        }
    }

    /**
     * 提取文件名
     *
     * @param url 完整 URL
     * @return 文件名
     */
    protected String extractFileName(String url) {
        String path = extractPath(url);
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
            return path.substring(lastSlashIndex + 1);
        }
        return path;
    }

    /**
     * 获取文件扩展名
     *
     * @param url 完整 URL
     * @return 扩展名
     */
    protected String getExtension(String url) {
        String fileName = extractFileName(url);
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 判断是否为图片
     *
     * @param url URL
     * @return 是否为图片
     */
    protected boolean isImage(String url) {
        String ext = getExtension(url);
        return "jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext)
                || "gif".equals(ext) || "webp".equals(ext) || "bmp".equals(ext);
    }
}
