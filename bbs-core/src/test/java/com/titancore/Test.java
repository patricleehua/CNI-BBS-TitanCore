package com.titancore;

import com.titancore.domain.dto.AiMessageDTO;
import com.titancore.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

@SpringBootTest
public class Test {

    @Autowired

    private AiChatService AiChatService;

    @org.junit.jupiter.api.Test
    void testChatAgent() {
        // 创建一个AiMessageDTO对象，用于封装用户输入的文本信息
        AiMessageDTO aiMessageDTO = new AiMessageDTO();
        aiMessageDTO.setAiSessionId("1");
        aiMessageDTO.setTextContent("你用的什么模型，讲一个笑话");
        aiMessageDTO.setEnableVectorStore(false);

        // 调用AiChatService的chatWithAgent方法，传入AiMessageDTO对象作为参数
        Flux<ServerSentEvent<String>> response = AiChatService.chatWithAgent(aiMessageDTO);


        // 收集第一个事件并阻塞等待
        ServerSentEvent<String> result = response.next().block();
        if (result != null) {
            System.out.println("Received: " + result.data());
        }
    }
}
