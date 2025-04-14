package com.titancore.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.fastjson.JSON;
import com.titancore.domain.dto.AiMessageDTO;
import com.titancore.domain.dto.AiMessageMediaDTO;
import com.titancore.enums.Functions;
import com.titancore.factory.ChatClientFactory;
import com.titancore.factory.VectorStoreFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@Service
public class AiChatService {
    @Resource
    private ChatClientFactory chatClientFactory;
    @Resource
    private VectorStoreFactory vectorStoreFactory;
    @Resource
    private AiMessageChatMemory chatMemory;
    @Resource
    private DashScopeChatModel dashScopeChatModel;
    private ChatClient chatClient;
    @PostConstruct
    public void init() {
        chatClient = chatClientFactory.createChatClient(Functions.AGENT, dashScopeChatModel);
    }
    public Flux<ServerSentEvent<String>> chatWithAgent(AiMessageDTO aiMessageDTO) {
        return chatClient
                .prompt()
                .user(promptUserSpec -> executeUserText(promptUserSpec,aiMessageDTO))
                .advisors(advisorSpec -> {
                    useChatHistory(advisorSpec, aiMessageDTO.getAiSessionId());
                    useVectorStore(advisorSpec, aiMessageDTO.isEnableVectorStore(),aiMessageDTO.getTextContent());
                })
                .system(promptSystemSpec -> promptSystemSpec.param("current_date", LocalDate.now().toString()))
                .stream()
                .content()
                .map(chatResponse -> ServerSentEvent.builder(JSON.toJSONString(chatResponse))
                        // 和前端监听的事件相对应
                        .event("message")
                        .build())
                .concatWith(Flux.just(ServerSentEvent.builder(JSON.toJSONString("[complete]")).event("message").build())) // 结束标志
              ;
    }
    private void executeUserText(ChatClient.PromptUserSpec promptSpec, AiMessageDTO aiMessageDTO){
        promptSpec.text(aiMessageDTO.getTextContent());
        List<AiMessageMediaDTO> medias = aiMessageDTO.getMedias();
        if(!medias.isEmpty()){
            // 用户发送的图片/语言
            List<Media> aiMedia = medias.stream().map(AiChatService::toSpringAiMedia).toList();
            Media[] media = new Media[aiMedia.size()];
            promptSpec.media(aiMedia.toArray(media));
        }
    }
    private void useChatHistory(ChatClient.AdvisorSpec advisorSpec, String sessionId) {
        // MessageChatMemoryAdvisor的三个重要参数
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，这里使用自己实现的AiMessageChatMemory，数据库存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，然后一起发送给大模型。
        advisorSpec.advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10));
    }
    private void useVectorStore(ChatClient.AdvisorSpec advisorSpec, Boolean enableVectorStore, String question) {
        if (!enableVectorStore) return;

        String promptWithContext = """
            下面是上下文信息
            ---------------------
            {question_answer_context}
            ---------------------
            给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。如果答案不在上下文中，告诉用户你不能回答这个问题。
            """;

        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression filterExpression = builder.in("question",
                Collections.singletonList("*" + question + "*")).build();

        System.out.println("Generated filter expression: " + filterExpression);

        // 先查元数据
        SearchRequest metaSearchRequest = SearchRequest
                .query(question)
                .withTopK(3) //指定要返回的相似文档的最大数量
                .withSimilarityThreshold(0.1)
                .withFilterExpression(filterExpression);
        VectorStore vectorStore = vectorStoreFactory.createElasticsearchVectorStore("es-rag");
        // 执行元数据查询
        var metaSearchResult = vectorStore.similaritySearch(metaSearchRequest);
        if (metaSearchResult.isEmpty()) {
            // 如果没有查到元数据，执行相似度搜索
            SearchRequest similaritySearchRequest = SearchRequest
                    .query(question)
                    .withTopK(3)
                    .withSimilarityThreshold(0.1);
            advisorSpec.advisors(new QuestionAnswerAdvisor(vectorStore, similaritySearchRequest, promptWithContext));
        } else {
            // 查到元数据，则使用找到的元数据构建问题答案的上下文
            advisorSpec.advisors(new QuestionAnswerAdvisor(vectorStore, metaSearchRequest, promptWithContext));
        }
    }
    @SneakyThrows
    public static Media toSpringAiMedia(AiMessageMediaDTO media) {
        return new Media(new MediaType(media.getType()), new URL(media.getUrl()));
    }
}
