package com.titancore.service.impl;


import com.titancore.domain.entity.AiMessage;
import com.titancore.domain.entity.AiMessageMedia;
import com.titancore.enums.AiMessageType;
import com.titancore.service.AiMessageService;
import com.titancore.service.AiSessionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 将对话消息保存进数据库.
 * 1.0.0正式版最新推荐
 * @author leehua
 * @since 2025/6/9
 */
@Service
@Slf4j
public class AiMessageChatMemoryRepository implements ChatMemoryRepository {

    private final AiSessionService aiSessionService;
    private final AiMessageService aiMessageService;
    public AiMessageChatMemoryRepository(AiSessionService aiSessionService, AiMessageService aiMessageService) {
        this.aiSessionService = aiSessionService;
        this.aiMessageService = aiMessageService;
    }

    @Override
    public List<String> findConversationIds() {
        return List.of();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        List<AiMessage> aiMessages = aiMessageService.getAiMessageListBySessionId(conversationId, 999999999);
        if (aiMessages != null){
            return aiMessages.stream()
                    .flatMap(aiMessage -> toSpringAiMessage(aiMessage).stream())
                    .toList();
        }
        return List.of();
    }

    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息
     */
    @Override
    public void saveAll(String conversationId, List<Message> messages) {

    }

    @Override
    public void deleteByConversationId(String conversationId) {
        aiMessageService.deleteSessionMessageBySessionId(conversationId);
    }

    /**
     * 将数据库的消息记录转换为Spring AI Message
     * 新逻辑: 一条记录只包含一个角色的消息(user或assistant)
     * - role=user: 用户消息存在user_text中,可能包含medias
     * - role=assistant: AI回复存在model_text中,思考过程存在thinking_text中
     */
    public static List<Message> toSpringAiMessage(AiMessage aiMessage) {
        List<Message> messages = new ArrayList<>();

        if (aiMessage.getType() == null) {
            log.warn("消息角色为空,跳过该消息: id={}", aiMessage.getId());
            return messages;
        }

        switch (Objects.requireNonNull(AiMessageType.valueOfAll(aiMessage.getType().getValue()))) {
            case USER -> {
                // 用户消息
                if (aiMessage.getTextContent() != null && !aiMessage.getTextContent().isEmpty()) {
                    List<Media> mediaList = new ArrayList<>();
                    if (aiMessage.getMedias() != null && !aiMessage.getMedias().isEmpty()) {
                        mediaList = aiMessage.getMedias().stream()
                                .map(AiMessageChatMemoryRepository::toSpringAiMedia)
                                .toList();
                    }
                    UserMessage userMessage = UserMessage.builder()
                            .text(aiMessage.getTextContent())
                            .media(mediaList)
                            .build();
                    messages.add(userMessage);
                }
            }
            case ASSISTANT -> {
                // AI回复消息
                if (aiMessage.getTextContent() != null && !aiMessage.getTextContent().isEmpty()) {
                    AssistantMessage assistantMessage = new AssistantMessage(aiMessage.getTextContent());
                    messages.add(assistantMessage);
                }
            }
            case SYSTEM -> {
                // 系统消息
                if (aiMessage.getTextContent() != null && !aiMessage.getTextContent().isEmpty()) {
                    org.springframework.ai.chat.messages.SystemMessage systemMessage =
                        new org.springframework.ai.chat.messages.SystemMessage(aiMessage.getTextContent());
                    messages.add(systemMessage);
                }
            }
            default -> log.warn("未知的消息角色: {}, 消息ID: {}", aiMessage.getType(), aiMessage.getId());
        }

        return messages;
    }
    @SneakyThrows
    public static Media toSpringAiMedia(AiMessageMedia media) {
        URL url = new URL(media.getUrl());
        MimeType mimeType = new MediaType(media.getType());
        return Media.builder()
                .mimeType(mimeType)
                .data(url.toURI())
                .build();
    }
}
