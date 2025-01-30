package com.titancore.service;

import com.titancore.advisor.ContainsOffendingWords;
import com.titancore.domain.entity.AiCheckOffendingWordsResponse;
import com.titancore.enums.Functions;
import com.titancore.factory.ChatClientFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;



@Service
public class AiDetectTextService {
    @Resource
    private ChatClientFactory chatClientFactory;

    @Resource
    private OpenAiChatModel openAiChatModel;
    private ChatClient chatClient;
    @PostConstruct
    public void init() {
        chatClient = chatClientFactory.createChatClient(Functions.DETECTTEXTAGENT, openAiChatModel);
    }

    public boolean isContainsOffendingWords(String text, String[] offendingWords){
        AiCheckOffendingWordsResponse entity = chatClient.prompt()
                .user(text)
                .advisors(new ContainsOffendingWords(offendingWords))
                .functions("recordOffendingWordsLog")
                .call()
                .entity(AiCheckOffendingWordsResponse.class);
        return entity.isViolation();
    }
}
