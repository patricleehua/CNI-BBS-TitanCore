package com.titancore.config.sensitiveword.service;

import com.github.houbb.sensitive.word.api.IWordDeny;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义敏感词 (黑名单)
 */
@Component
@Slf4j
public class CniWordDeny implements IWordDeny {
    /**
     * 注意每一行为一个敏感词，单行不能只包括空格，否则，也会把空格识别为敏感词
     * @return
     */
    @Override
    public List<String> deny() {
        List<String> list = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("sensitive/DenyWords.txt");
            try (var inputStream = resource.getInputStream();
                 var reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        list.add(line);
                    }
                }
            }
        } catch (IOException e) {
            log.error("读取敏感词文件错误: {}", e.getMessage());
        }
        return list;
    }
}
