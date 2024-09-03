package com.titancore.framework.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

public class JsonUtils {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        /**
         * 如果出参中含有 Java 8 新的日期 API, 如 LocalDateTime , 你可能遇到过下面这个异常：
         * com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.quanxiaoha.framework.common.response.Response["data"]->com.quanxiaoha.xiaohashu.auth.controller.User["createTime"])
         * 这是由于 Jackson 本身不支持新的日期 API , 需要使用 jackson-datatype-jsr310 库来解决此问题。
         */
        OBJECT_MAPPER.registerModules(new JavaTimeModule()); // 解决 LocalDateTime 的序列化问题

    }

    /**
     *  将对象转换为 JSON 字符串
     * @param obj
     * @return
     */
    @SneakyThrows
    public static String toJsonString(Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }
    /**
     * 初始化：统一使用 Spring Boot 个性化配置的 ObjectMapper
     *
     * @param objectMapper
     */
    public static void init(ObjectMapper objectMapper) {
        OBJECT_MAPPER = objectMapper;
    }
}
