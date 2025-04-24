package com.titancore.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.titancore.advisor.ContainsOffendingWords;
import com.titancore.domain.entity.AiCheckOffendingWordsResponse;
import com.titancore.enums.Functions;
import com.titancore.factory.ChatClientFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class AiDetectTextService {
    @Resource
    private ChatClientFactory chatClientFactory;

    @Resource
    private OpenAiChatModel openAiChatModel;
    @Resource
    private DashScopeChatModel dashScopeChatModel;
    private ChatClient chatClient;
    @PostConstruct
    public void init() {
        chatClient = chatClientFactory.createChatClient(Functions.DETECTTEXTAGENT, dashScopeChatModel);
    }

    public AiCheckOffendingWordsResponse isContainsOffendingWords(String text, Set<String> offendingWords){
        return chatClient.prompt()
                .user(text)
                .advisors(new ContainsOffendingWords(offendingWords))
                .functions("recordOffendingWordsLog")
                .call()
                .entity(AiCheckOffendingWordsResponse.class);
    }
}
