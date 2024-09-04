package com.titancore.framework.common.util;

public class Base64UrlUtils {

    /**
     * 将 Base64 URL 编码字符串转换为标准 Base64 编码
     * @param base64Url - Base64 URL 编码字符串
     * @return 标准 Base64 编码字符串
     */
    public static String base64UrlToBase64(String base64Url) {
        // 替换 URL 安全的字符
        String base64 = base64Url.replace('_', '/').replace('-', '+');
        // 处理填充字符
        int paddingLength = 4 - (base64.length() % 4);
        if (paddingLength < 4) {
            base64 += "=".repeat(paddingLength);
        }
        return base64;
    }

    /**
     * 将标准 Base64 编码字符串转换为 Base64 URL 编码
     * @param base64 - 标准 Base64 编码字符串
     * @return Base64 URL 编码字符串
     */
    public static String base64ToBase64Url(String base64) {
        return base64.replace('/', '_').replace('+', '-').replaceAll("=+$", "");
    }

}
