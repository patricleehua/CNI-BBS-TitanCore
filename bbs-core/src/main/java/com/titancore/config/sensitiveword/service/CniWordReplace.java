package com.titancore.config.sensitiveword.service;

import com.github.houbb.sensitive.word.api.IWordContext;
import com.github.houbb.sensitive.word.api.IWordReplace;
import com.github.houbb.sensitive.word.api.IWordResult;
import com.github.houbb.sensitive.word.utils.InnerWordCharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 自定义敏感词对应的替换值.
 * 场景说明：有时候我们希望不同的敏感词有不同的替换结果。比如【游戏】替换为【电子竞技】，【失业】替换为【灵活就业】。
 */
@Component
@Slf4j
public class CniWordReplace implements IWordReplace {

    private Map<String, String> replaceWordsMap;

    public CniWordReplace() {
        // 加载敏感词和替换词的映射
        this.replaceWordsMap = loadReplaceWords();
    }

    @Override
    public void replace(StringBuilder stringBuilder, char[] chars, IWordResult iWordResult, IWordContext iWordContext) {
        String sensitiveWord = InnerWordCharUtils.getString(chars, iWordResult);

        // 如果敏感词在替换词映射中，则使用替换词
        if (replaceWordsMap.containsKey(sensitiveWord)) {
            stringBuilder.append(replaceWordsMap.get(sensitiveWord));
        } else {
            // 否则默认使用 * 代替
            int wordLength = iWordResult.endIndex() - iWordResult.startIndex();
            for (int i = 0; i < wordLength; i++) {
                stringBuilder.append("*");
            }
        }
    }

    /**
     * 从文件中加载敏感词及其替换词
     * 文件的格式应该是：敏感词=替换词
     *
     * @return 一个 Map，键为敏感词，值为替换词
     */
    private Map<String, String> loadReplaceWords() {
        Map<String, String> map = new HashMap<>();
        try {
            // 读取 classpath 下的 ReplaceWords.txt 文件
            ClassPathResource resource = new ClassPathResource("sensitive/ReplaceWords.txt");
            Path filePath = Path.of(resource.getFile().getPath());
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            // 处理文件的每一行
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue; // 忽略空行
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String word = parts[0].trim();
                    String replacement = parts[1].trim();
                    map.put(word, replacement);
                }
            }
        } catch (IOException e) {
            log.error("读取 ReplaceWords.txt 文件时发生错误: {}", e.getMessage());
        }
        return map;
    }
}
