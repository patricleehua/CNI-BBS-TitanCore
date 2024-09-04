//package com.titancore.framework.webmvc.interceptor;
//
//
//import com.alibaba.fastjson.JSON;
//import com.titancore.framework.common.properties.AESKey;
//import com.titancore.framework.common.properties.AesUtils;
//import com.titancore.framework.common.util.Base64UrlUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//import java.nio.charset.StandardCharsets;
//import java.security.SecureRandom;
//import java.util.Base64;
//import java.util.HashSet;
//import java.util.Set;
//
//@ControllerAdvice
//public class ResponseEncryptionAdvice implements ResponseBodyAdvice<Object> {
//
//    @Autowired
//    private AESKey aesKey;
//
//    // 白名单路径集合
//    private static final Set<String> WHITELIST_PATHS = new HashSet<>();
//
//    static {
//        // 在这里添加需要放行的路径
//        WHITELIST_PATHS.add("/system/verif/gen/random");
//        WHITELIST_PATHS.add("/system/verif/check3");
//        WHITELIST_PATHS.add("/common/upload");
//    }
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        // 决定是否需要加密响应体
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
//                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
//                                  ServerHttpRequest request, ServerHttpResponse response) {
//        String requestURI = request.getURI().getPath();
//
//        // 判断是否需要跳过加密
//        if (WHITELIST_PATHS.contains(requestURI)) {
//            return body; // 跳过加密
//        }
//        //是否为POST,不是POST不加密
////        if (!"POST".equalsIgnoreCase(String.valueOf(request.getMethod()))) {
////            return body; // 放行非POST请求，不再执行后续逻辑
////        }
//        // 生成新的 IV
//        byte[] iv = new byte[16];
//        new SecureRandom().nextBytes(iv);
//
//        // 设置 IV 到响应头
//        response.getHeaders().add("iv",
//                Base64UrlUtils
//                        .base64ToBase64Url(
//                                Base64.getEncoder().encodeToString(iv)));
//
//        // 加密响应体
//        if (body != null) {
//            try {
//                String responseBody = JSON.toJSONString(body);
//                byte[] encryptedData = AesUtils.encrypt(Base64.getDecoder().decode(aesKey.getSecretKey()), iv, responseBody.getBytes());
//                    return Base64UrlUtils
//                        .base64ToBase64Url(
//                                Base64.getEncoder().encodeToString(encryptedData));
//            } catch (Exception e) {
//                throw new RuntimeException("Encryption failed", e);
//            }
//        }
//
//        return body;
//    }
//
//    public static void main(String[] args) {
//        try {
//            // 生成新的 IV
//            byte[] iv = new byte[16];
//            new SecureRandom().nextBytes(iv);
//
//            // 定义加密密钥
//            String secretKey = "eW1awsJiSLH7CGnmPGvBQZyPmACHTQYImNJPAu34fzs";
//            byte[] key = Base64.getDecoder().decode(secretKey);
//
//            // 要加密的内容
//            String body = "{\n" +
//                    "  \"username\": \"spring\",\n" +
//                    "  \"password\": \"123456\"\n" +
//                    "}";
//            byte[] data = body.getBytes(StandardCharsets.UTF_8);
//
//
//            // 加密
//            byte[] encryptedData = AesUtils.encrypt(key, iv, data);
//            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedData);
//            String ivBase64 = Base64.getEncoder().encodeToString(iv);
//
//            System.out.println("Encrypted data: " + encryptedBase64);
//            System.out.println("IV: " + ivBase64);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
