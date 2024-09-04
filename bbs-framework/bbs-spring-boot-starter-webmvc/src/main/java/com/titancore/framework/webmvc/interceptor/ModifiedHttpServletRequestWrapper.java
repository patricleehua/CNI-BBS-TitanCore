package com.titancore.framework.webmvc.interceptor;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 扩展 HttpServletRequestWrapper，以修改 HTTP 请求中的请求体内容。
 */
public class ModifiedHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final String modifiedBody;

    public ModifiedHttpServletRequestWrapper(HttpServletRequest request, String modifiedBody) {
        super(request);
        this.modifiedBody = modifiedBody;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(modifiedBody.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStreamWrapper(new ByteArrayInputStream(modifiedBody.getBytes(StandardCharsets.UTF_8)));
    }
}
