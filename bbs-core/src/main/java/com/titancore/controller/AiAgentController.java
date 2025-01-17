package com.titancore.controller;

import com.titancore.domain.dto.AiMessageDTO;
import com.titancore.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequestMapping("/aiService")
@RestController
@Slf4j
@Tag(name = "AI服务支持")
public class AiAgentController {
    @Resource
    private AiChatService AiChatService;
    @PostMapping(value = "/chatAgent", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "智能客服聊天接口" )
    public Flux<ServerSentEvent<String>> chatStreamWithHistory(@RequestBody AiMessageDTO aiMessageDTO) {
        return AiChatService.chatWithAgent(aiMessageDTO);
    }
}
