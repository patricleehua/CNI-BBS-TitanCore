package com.titancore.config.sensitiveword.service;

import com.github.houbb.sensitive.word.api.IWordAllow;
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
 * 自定义非敏感词 (白名单)
 */
@Component
@Slf4j
public class CniWordAllow  implements IWordAllow {
    /**
     *
     * 注意每一行为一个非敏感词，单行不能只包括空格，否则，也会把空格识别为非敏感词
     * @return
     */
    @Override
    public List<String> allow() {
        List<String> list = new ArrayList<>();
        try {
            Resource myAllowWords = new ClassPathResource("sensitive/AllowWords.txt");
            Path myAllowWordsPath = Paths.get(myAllowWords.getFile().getPath());
            list =  Files.readAllLines(myAllowWordsPath, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("读取非敏感词文件错误:{}",ioException.getMessage());
        }
        return list;
    }
}
