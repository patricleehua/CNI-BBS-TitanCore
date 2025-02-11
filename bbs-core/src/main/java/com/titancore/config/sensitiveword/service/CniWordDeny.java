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
            Resource mySensitiveWords = new ClassPathResource("sensitive/DenyWords.txt");
            Path mySensitiveWordsPath = Paths.get(mySensitiveWords.getFile().getPath());
            list =  Files.readAllLines(mySensitiveWordsPath, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("读取敏感词文件错误:{}",ioException.getMessage());
        }
        return list;
    }
}
