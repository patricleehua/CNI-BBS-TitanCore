package com.titancore.ai.retriever;


import com.titancore.domain.dto.RemoteEmbeddingSearchDTO;
import com.titancore.service.impl.AiMessageChatMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

/**
 * ChatProvider Advisor 公共助手类.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatProviderAdvisorHelper {

    private final AiMessageChatMemoryRepository chatMemoryRepository;

    /**
     * 配置聊天历史记录 Advisor.
     *
     * @param advisorSpec  Advisor规范
     * @param sessionId    会话ID
     * @param historySize  历史轮数 (0表示不使用历史)
     */
    public void addChatHistoryAdvisor(ChatClient.AdvisorSpec advisorSpec, String sessionId, int historySize) {
        if (historySize <= 0) {
            return;
        }

        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(historySize)
                .build();
        MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(sessionId)
                .scheduler(Schedulers.boundedElastic())
                .build();

        advisorSpec.advisors(advisor);
    }

    /**
     * 配置 RAG 检索增强 Advisor.
     *
     * @param advisorSpec                   Advisor规范
     * @param enableRag                     是否启用RAG
     * @param ragType                       RAG类型 (local/aliyun/custom)
     * @param ragSearchConfig               RAG检索配置
     */
    public void addRagAdvisors(ChatClient.AdvisorSpec advisorSpec, Boolean enableRag, String ragType,
                               RemoteEmbeddingSearchDTO ragSearchConfig) {
        if (!Boolean.TRUE.equals(enableRag)) {
            return;
        }

        if (ragSearchConfig == null || ragSearchConfig.getDocumentIndexIds().isEmpty()) {
            return;
        }
        //todo 待完善
        var ragAdvisors = RagAdvisorFactory.createRagAdvisors();
        ragAdvisors.forEach(advisorSpec::advisors);
    }
}
