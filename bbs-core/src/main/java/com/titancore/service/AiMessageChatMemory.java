package com.titancore.service;

import com.titancore.domain.entity.AiMessage;
import com.titancore.domain.entity.AiMessageMedia;
import com.titancore.enums.AiMessageType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 将对话消息保存进数据库
 */
@Slf4j
@Service
public class AiMessageChatMemory implements ChatMemory {

    private final AiSessionService aiSessionService;
    private final AiMessageService aiMessageService;
    public AiMessageChatMemory(AiSessionService aiSessionService, AiMessageService aiMessageService) {
        this.aiSessionService = aiSessionService;
        this.aiMessageService = aiMessageService;
    }
    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages){
            AiMessage aiMessage = aiMessageService.convertToAiMessage(message);
            aiMessage.setAiSessionId(Long.valueOf(conversationId));
            aiMessage.setCreatorId(1L);
            aiMessage.setEditorId(1L);
            aiMessageService.save(aiMessage);
        }
    }
    /**
     * 查询会话内的消息最新n条历史记录（提供给AI模型）
     *
     * @param conversationId 会话id
     * @param lastN          最近n条
     * @return org.springframework.ai.chat.messages.Message格式的消息
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<AiMessage> aiMessages = aiMessageService.getAiMessageListBySessionId(conversationId, lastN);
        if (aiMessages != null){
            return aiMessages.stream().map(AiMessageChatMemory::toSpringAiMessage).toList();
        }
        return List.of();
    }
    /**
     * 清除会话内的消息
     *
     * @param conversationId 会话id
     */
    @Override
    public void clear(String conversationId) {
        aiMessageService.deleteSessionMessageBySessionId(conversationId);
    }

    public static Message toSpringAiMessage(AiMessage aiMessage) {
        List<Media> mediaList = new ArrayList<>();
        if (!(aiMessage.getMedias() == null)) {
            mediaList = aiMessage.getMedias().stream().map(AiMessageChatMemory::toSpringAiMedia).toList();
        }
        AiMessageType aiMessageType = aiMessage.getType();
        switch (aiMessageType){
            case USER -> {
                return new UserMessage(aiMessage.getTextContent(), mediaList);
            }
            case ASSISTANT -> {
                return new AssistantMessage(aiMessage.getTextContent());
            }
            case SYSTEM -> {
                return new SystemMessage(aiMessage.getTextContent());
            }
            default -> throw new RuntimeException("不支持的消息类型");
        }

    }
    @SneakyThrows
    public static Media toSpringAiMedia(AiMessageMedia media) {
        return new Media(new MediaType(media.getType()), new URL(media.getUrl()));
    }
}
