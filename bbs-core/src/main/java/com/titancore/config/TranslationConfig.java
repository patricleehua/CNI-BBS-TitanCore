package com.titancore.config;

import com.titancore.framework.common.translation.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 翻译配置类
 * 应用启动时注册字典数据
 *
 * @author TitanCore
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TranslationConfig implements ApplicationRunner {

    private final TranslationService translationService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 注册用户状态字典
        Map<Object, String> userStatusMap = new HashMap<>();
        userStatusMap.put(0, "禁用");
        userStatusMap.put(1, "启用");
        translationService.registerDictionary("user_status", userStatusMap);

        // 注册帖子类型字典
        Map<Object, String> postTypeMap = new HashMap<>();
        postTypeMap.put("original", "原创");
        postTypeMap.put("repost", "转载");
        postTypeMap.put("translation", "翻译");
        translationService.registerDictionary("post_type", postTypeMap);

        // 注册支付方式字典
        Map<Object, String> payTypeMap = new HashMap<>();
        payTypeMap.put("alipay", "支付宝");
        payTypeMap.put("wechat", "微信支付");
        payTypeMap.put("unionpay", "银联支付");
        translationService.registerDictionary("pay_type", payTypeMap);

        log.info("翻译字典注册完成");
    }
}
