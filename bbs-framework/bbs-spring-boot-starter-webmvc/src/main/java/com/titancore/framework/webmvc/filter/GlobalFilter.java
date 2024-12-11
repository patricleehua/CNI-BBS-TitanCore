package com.titancore.framework.webmvc.filter;


import com.titancore.framework.common.properties.AESKey;
import com.titancore.framework.common.util.AesUtils;
import com.titancore.framework.common.util.Base64UrlUtils;
import com.titancore.framework.webmvc.interceptor.ModifiedHttpServletRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@WebFilter(filterName = "GlobalFilter", urlPatterns = {"/**"})
@Component
public class GlobalFilter implements Filter {

    private static final Set<String> WHITELIST_PATHS = new HashSet<>();

    static {
        WHITELIST_PATHS.add("/system/verif/gen/random");
        WHITELIST_PATHS.add("/system/verif/check3");
        WHITELIST_PATHS.add("/common/upload");
        //swagger
        WHITELIST_PATHS.add("/favicon.ico");
        WHITELIST_PATHS.add("/swagger-resources/**");
        WHITELIST_PATHS.add("/webjars/**");
        WHITELIST_PATHS.add("/v3/**");
        WHITELIST_PATHS.add("/doc.html");
        //第三方认证
        WHITELIST_PATHS.add("/auth/**");
    }

    @Autowired
    private AESKey aesKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 上传文件不做解密处理
        if (httpRequest.getHeader("Content-Type").startsWith("multipart/form-data")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查请求路径是否在白名单中
        String requestURI = httpRequest.getRequestURI();
        for (String path : WHITELIST_PATHS) {
            if(path.endsWith("/**")){
                String prePath = path.substring(0, path.length() - 3);
                if(requestURI.startsWith(prePath)){
                    chain.doFilter(request, response);
                    return;
                }
            } else if (requestURI.equals(path)) {
                chain.doFilter(request, response);
                return;
            }
        }


        // 只拦截POST请求
        if (!"POST".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return; // 放行非POST请求，不再执行后续逻辑
        }

        // 读取请求体
        String requestBody = readRequestBody(httpRequest);

        // 处理双引号
        requestBody = requestBody.replace("\"", ""); // 可选择性移除双引号，如果需要

        // 获取IV
        String IV = httpRequest.getHeader("iv");
        if (IV == null || IV.isEmpty()) {
            return; //非IV加密
//            throw new IllegalRequestException("不合法的请求：缺少IV");
        }
        String decodeBase64Body = Base64UrlUtils.base64UrlToBase64(requestBody);
        String decodeBase64BodyIV = Base64UrlUtils.base64UrlToBase64(IV);
        // 解密请求体
        byte[] decrypt = decrypt(decodeBase64Body
                ,decodeBase64BodyIV );

        String decryptedString = new String(decrypt, StandardCharsets.UTF_8);

        // 使用修改后的请求体创建新的请求对象
        ModifiedHttpServletRequestWrapper modifiedRequest = new ModifiedHttpServletRequestWrapper(httpRequest, decryptedString);

        // 继续处理请求
        chain.doFilter(modifiedRequest, response);
    }

    private byte[] decrypt(String encryptedText, String IV) throws IOException {
        try {
            byte[] key = Base64.getDecoder().decode(aesKey.getSecretKey());
            byte[] iv = Base64.getDecoder().decode(IV);
            byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
            return AesUtils.decrypt(key, iv, encryptedData);
        } catch (Exception e) {
            throw new IOException("解密失败", e);
        }
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        // 读取请求体的内容
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

//    public static void main(String[] args) {
//        try {
//            // Base64 解码 IV
//            String ivBase64 = "SfM1WDtbLKneSEvOSiGpkg==";
//            byte[] iv = Base64.getDecoder().decode(Base64UrlUtils.base64UrlToBase64(ivBase64));
//
//            // Base64 解码密钥
//            String secretKey = "eW1awsJiSLH7CGnmPGvBQZyPmACHTQYImNJPAu34fzs";
//            byte[] key = Base64.getDecoder().decode(secretKey);
//
//            // Base64 解码加密数据
//            String encryptedBase64 = "8MuzAHhtkSZ0Y3q6B8BX6bLHguTVmBLamHpNe5/LVD/Q0xmoeFmnh8jClddGw3ncen9xK7ZnlmZOtPmTiMpo6vFkdFSFo4CV0JwJIJzLtNmdPKowz0adnfo/ryNrE4P5C2iqcJvrbFHYp4VErm9Dyas/1tHveyyZNmTjiypn/LH7yKyGfCyLiuKlNw8xLYk9bMtWknzkJWPI+nB1XdoWe8TuNAn6mFNtz0xMm0VRe3gng5N5RUIC/Xw8z8HY44M5VESnIgLM6WjeNoIb7VWT83jFdpT25+fLzYOhew==";
//            byte[] encryptedData = Base64.getDecoder().decode(Base64UrlUtils.base64UrlToBase64(encryptedBase64));
//
//            // 解密
//            byte[] decryptedData = AesUtils.decrypt(key, iv, encryptedData);
//            String decryptedString = new String(decryptedData, StandardCharsets.UTF_8);
//            System.out.println(decryptedString);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

