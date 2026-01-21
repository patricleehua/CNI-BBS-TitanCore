package com.titancore.service;

import com.google.common.io.Resources;
import com.titancore.ai.router.ModelRouter;
import com.titancore.domain.dto.AiMessageDTO;
import com.titancore.domain.dto.UnifiedChatRequest;
import com.titancore.provider.ChatModelProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ModelRouter modelRouter;

    private String agentSystemPrompt;

    @PostConstruct
    public void init() throws IOException {
        this.agentSystemPrompt = Resources.toString(
                Resources.getResource("prompts/agent-base-message.st"),
                StandardCharsets.UTF_8
        );
    }


    public Flux<ServerSentEvent<String>> chatWithAgent(AiMessageDTO aiMessageDTO) {
        // 获取当前日期并格式化
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String systemPrompt = agentSystemPrompt.replace("{current_date}", currentDate);
        // 构建统一请求对象
        UnifiedChatRequest request = UnifiedChatRequest.builder()
                .sessionId(aiMessageDTO.getAiSessionId())
                .systemPrompt(systemPrompt)
                .message(aiMessageDTO.getTextContent())
                .stream(true)
                .historySize(10)
                .enableRag(aiMessageDTO.isEnableVectorStore())
                .medias(aiMessageDTO.getMedias() != null ? aiMessageDTO.getMedias() : new ArrayList<>())
                .build();

        // 智能路由选择Provider
        ChatModelProvider provider = modelRouter.selectProvider(request);

        // 创建ChatClient并执行流式聊天
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 1.0);
        return provider.chatStream(request, provider.createChatClient(request.getModel(), options));
    }


}
