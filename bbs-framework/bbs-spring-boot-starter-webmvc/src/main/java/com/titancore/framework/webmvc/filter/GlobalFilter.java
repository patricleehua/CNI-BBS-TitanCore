//package com.titancore.framework.webmvc.filter;
//
//
//import com.titancore.framework.common.properties.AESKey;
//import com.titancore.framework.common.properties.AesUtils;
//import com.titancore.framework.common.util.Base64UrlUtils;
//import com.titancore.framework.webmvc.interceptor.ModifiedHttpServletRequestWrapper;
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.HashSet;
//import java.util.Set;
//
//@Slf4j
//@WebFilter(filterName = "GlobalFilter", urlPatterns = {"/**"})
//@Component
//public class GlobalFilter implements Filter {
//
//    private static final Set<String> WHITELIST_PATHS = new HashSet<>();
//
//    static {
//        WHITELIST_PATHS.add("/system/verif/gen/random");
//        WHITELIST_PATHS.add("/system/verif/check3");
//        WHITELIST_PATHS.add("/common/upload");
//    }
//
//    @Autowired
//    private AESKey aesKey;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//
//        // 上传文件不做解密处理
//        if ("multipart/form-data".equals(httpRequest.getHeader("Content-Type"))) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // 检查请求路径是否在白名单中
//        String requestURI = httpRequest.getRequestURI();
//        if (WHITELIST_PATHS.contains(requestURI)) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // 只拦截POST请求
//        if (!"POST".equalsIgnoreCase(httpRequest.getMethod())) {
//            chain.doFilter(request, response);
//            return; // 放行非POST请求，不再执行后续逻辑
//        }
//
//        // 读取请求体
//        String requestBody = readRequestBody(httpRequest);
//
//        // 处理双引号
//        requestBody = requestBody.replace("\"", ""); // 可选择性移除双引号，如果需要
//
//        // 获取IV
//        String IV = httpRequest.getHeader("iv");
//        if (IV == null || IV.isEmpty()) {
//            return; //非IV加密
////            throw new IllegalRequestException("不合法的请求：缺少IV");
//        }
//        String decodeBase64Body = Base64UrlUtils.base64UrlToBase64(requestBody);
//        String decodeBase64BodyIV = Base64UrlUtils.base64UrlToBase64(IV);
//        // 解密请求体
//        byte[] decrypt = decrypt(decodeBase64Body
//                ,decodeBase64BodyIV );
//
//        String decryptedString = new String(decrypt, StandardCharsets.UTF_8);
//
//        // 使用修改后的请求体创建新的请求对象
//        ModifiedHttpServletRequestWrapper modifiedRequest = new ModifiedHttpServletRequestWrapper(httpRequest, decryptedString);
//
//        // 继续处理请求
//        chain.doFilter(modifiedRequest, response);
//    }
//
//    private byte[] decrypt(String encryptedText, String IV) throws IOException {
//        try {
//            byte[] key = Base64.getDecoder().decode(aesKey.getSecretKey());
//            byte[] iv = Base64.getDecoder().decode(IV);
//            byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
//            return AesUtils.decrypt(key, iv, encryptedData);
//        } catch (Exception e) {
//            throw new IOException("解密失败", e);
//        }
//    }
//
//    private String readRequestBody(HttpServletRequest request) throws IOException {
//        // 读取请求体的内容
//        StringBuilder stringBuilder = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//        }
//        return stringBuilder.toString();
//    }
//
//    public static void main(String[] args) {
//        try {
//            // Base64 解码 IV
//            String ivBase64 = "7S7tmPZNZjBgfWEgE8l_kg";
//            byte[] iv = Base64.getDecoder().decode(Base64UrlUtils.base64UrlToBase64(ivBase64));
//
//            // Base64 解码密钥
//            String secretKey = "eW1awsJiSLH7CGnmPGvBQZyPmACHTQYImNJPAu34fzs";
//            byte[] key = Base64.getDecoder().decode(secretKey);
//
//            // Base64 解码加密数据
//            String encryptedBase64 = "lzgd12QMG71ReWHi85Rn8vTykMPkh9RHEDOXTrh6SPcNQQZbNLR63lLPQn87KoE7ddjTEEBqbMXepyvK6ejlglD5HMSAd3lPpHrL70-hoCTazh4gUtLdeSH2YTRGESvOZMAKEZbrdJS1DPXdEsDWVK2GBAkhkY_T-R_l2_nDFJNk5umlR9nWDMhuf2iv9qGPkYJ1yH9Q7-8Y_k2FPh_fxAPv-QA2DpP2szbVomMe2azlKduXavD0ucHfU_mqU1qNhC16X_Hqb_6RDk7zRurxmC4H1oAH2P3lyPoTtZyBI2LFx8cyCPAwHb9avSAHwEN8zKSENP35tw7ou5S3iWS5sUdw";
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
//}
//
