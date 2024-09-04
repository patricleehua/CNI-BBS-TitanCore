package com.titancore.framework.webmvc.interceptor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * 包装 ServletInputStream，以支持从底层读取数据流。
 */
public class ServletInputStreamWrapper extends ServletInputStream {
    private final InputStream inputStream;

    public ServletInputStreamWrapper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public boolean isFinished() {
        try {
            return inputStream.available() == 0;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        // Not needed for this implementation
    }
}
