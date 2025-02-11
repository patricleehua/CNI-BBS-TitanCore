package com.titancore.config.sensitiveword;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import com.titancore.config.sensitiveword.service.CniWordAllow;
import com.titancore.config.sensitiveword.service.CniWordDeny;
import com.titancore.config.sensitiveword.service.CniWordReplace;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SensitiveWordBsConfig {

    @Resource
    private CniWordAllow cniWordAllow;
    @Resource
    private CniWordDeny cniWordDeny;
    @Resource
    private CniWordReplace cniWordReplace;
    
    /**
     * 初始化引导类
     * @return 初始化引导类
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        log.info("=====敏感词检查初始化=====");
        SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
//                .wordAllow(WordAllows.chains(WordAllows.defaults(), myWordAllow)) // 设置多个敏感词，系统默认和自定义
//                .wordDeny(WordDenys.chains(WordDenys.defaults(), myWordDeny))     // 设置多个敏感词，系统默认和自定义
                .wordAllow(WordAllows.chains(cniWordAllow))  // 自定义
                .wordDeny(WordDenys.chains(cniWordDeny))     // 自定义
                .wordReplace(cniWordReplace)                                        // 自定义替换规则
                .ignoreCase(true)           // 忽略大小写
                .ignoreWidth(true)          // 忽略半角圆角
                .ignoreNumStyle(true)       // 忽略数字的写法
                .ignoreChineseStyle(true)   // 忽略中文的书写格式
                .ignoreEnglishStyle(true)   // 忽略英文的书写格式
                .ignoreRepeat(true)         // 忽略重复词
                .enableNumCheck(true)       // 是否启用数字检测。默认连续 8 位数字认为是敏感词
                .enableEmailCheck(true)     // 是有启用邮箱检测
                .enableUrlCheck(true)       // 是否启用链接检测
                .init();
        return sensitiveWordBs;
    }
}
