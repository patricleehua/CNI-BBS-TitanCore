package com.titancore.factory;

import com.titancore.enums.Functions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ChatClientFactory {

    @Value("classpath:prompts/agent-base-message.st")
    private Resource agentResource;
    @Value("classpath:prompts/assistant-base-message.st")
    private Resource assistantResource;

    public ChatClient createChatClient(Functions provider, ChatModel model) {
        return switch (provider) {
            case AGENT -> ChatClient.builder(model)
                    .defaultSystem(agentResource)
                    .build();
            case ASSISTANT -> ChatClient.builder(model)
                    .defaultSystem(assistantResource)
                    .build();
        };
    }
}
