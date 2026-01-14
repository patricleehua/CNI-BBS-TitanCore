package com.titancore.service;


import com.titancore.ai.router.ModelRouter;
import com.titancore.domain.dto.UnifiedChatRequest;
import com.titancore.domain.entity.AiCheckOffendingWordsResponse;
import com.titancore.provider.ChatModelProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AiDetectTextService {

    private final ModelRouter modelRouter;

    public AiCheckOffendingWordsResponse isContainsOffendingWords(String text, Set<String> offendingWords) {
        // 构建提示词
        String prompt = buildDetectionPrompt(text, offendingWords);

        // 构建统一请求对象
        UnifiedChatRequest request = UnifiedChatRequest.builder()
                .message(prompt)
                .stream(false)
                .historySize(0)
                .systemPrompt("你是一个专业的内容审核助手，负责检测文本中的违禁词和不当内容。")
                .build();

        // 智能路由选择Provider
        ChatModelProvider provider = modelRouter.selectProvider(request);

        // 创建ChatClient并执行同步调用
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.3); // 降低温度以获得更稳定的结果

        try {
            var response = provider.chatSync(request, provider.createChatClient(request.getModel(), options));

            if (response.isSuccess() && response.getData() != null) {
                String content = response.getData().getContent();
                return parseDetectionResult(content);
            } else {
                // AI调用失败时返回保守结果
                AiCheckOffendingWordsResponse result = new AiCheckOffendingWordsResponse();
                result.setViolation(false);
                result.setReason("AI检测服务暂时不可用");
                return result;
            }
        } catch (Exception e) {
            // 异常时返回保守结果
            AiCheckOffendingWordsResponse result = new AiCheckOffendingWordsResponse();
            result.setViolation(false);
            result.setReason("AI检测异常: " + e.getMessage());
            return result;
        }
    }

    /**
     * 构建检测提示词
     */
    private String buildDetectionPrompt(String text, Set<String> offendingWords) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请检测以下文本是否包含违禁词或不当内容：\n\n");
        prompt.append("文本内容：").append(text).append("\n\n");

        if (offendingWords != null && !offendingWords.isEmpty()) {
            prompt.append("已检测到的敏感词：").append(String.join(", ", offendingWords)).append("\n\n");
        }

        prompt.append("请以JSON格式返回检测结果，格式如下：\n");
        prompt.append("{\n");
        prompt.append("  \"isViolation\": true/false,\n");
        prompt.append("  \"reason\": \"违规原因或通过说明\"\n");
        prompt.append("}\n\n");
        prompt.append("判断标准：\n");
        prompt.append("1. 是否包含政治敏感内容\n");
        prompt.append("2. 是否包含色情低俗内容\n");
        prompt.append("3. 是否包含暴力血腥内容\n");
        prompt.append("4. 是否包含辱骂诽谤内容\n");
        prompt.append("5. 是否包含违法违规内容\n");

        return prompt.toString();
    }

    /**
     * 解析AI返回的检测结果
     */
    private AiCheckOffendingWordsResponse parseDetectionResult(String content) {
        AiCheckOffendingWordsResponse result = new AiCheckOffendingWordsResponse();

        try {
            // 尝试从JSON中提取结果
            if (content.contains("\"isViolation\"")) {
                boolean isViolation = content.toLowerCase().contains("\"isviolation\": true") ||
                                     content.toLowerCase().contains("\"isviolation\":true");
                result.setViolation(isViolation);

                // 提取reason
                int reasonStart = content.indexOf("\"reason\":");
                if (reasonStart > 0) {
                    int valueStart = content.indexOf("\"", reasonStart + 9);
                    int valueEnd = content.indexOf("\"", valueStart + 1);
                    if (valueStart > 0 && valueEnd > valueStart) {
                        String reason = content.substring(valueStart + 1, valueEnd);
                        result.setReason(reason);
                    }
                }
            } else {
                // 如果没有JSON格式，根据关键词判断
                result.setViolation(content.toLowerCase().contains("违规") ||
                                   content.toLowerCase().contains("violation"));
                result.setReason(content);
            }
        } catch (Exception e) {
            // 解析失败时保守处理
            result.setViolation(false);
            result.setReason("解析AI响应失败: " + e.getMessage());
        }

        return result;
    }
}
