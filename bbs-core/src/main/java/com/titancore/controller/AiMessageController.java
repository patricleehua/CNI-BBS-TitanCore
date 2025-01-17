package com.titancore.controller;

import com.titancore.domain.dto.AiMessageDTO;
import com.titancore.domain.vo.AiMessageVo;
import com.titancore.service.AiMessageChatMemory;
import com.titancore.service.AiMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/aiMessage")
@RestController
@Slf4j
@Tag(name = "AI本地消息管理")
public class AiMessageController {

    @Resource
    private AiMessageService aiMessageService;
    @Resource
    private AiMessageChatMemory chatMemory;
    @PostMapping("/save")
    @Operation(summary = "保存消息")
    public void save(@RequestBody AiMessageDTO aiMessageDTO) {
        aiMessageService.saveAiMessage(aiMessageDTO);
    }

    @GetMapping("/history")
    @Operation(summary = "获取历史消息")
    public List<AiMessageVo> getAiMessageListBySessionId(@RequestParam String sessionId, @RequestParam int lastN) {
        return aiMessageService.getAiMessageListVoBySessionId(sessionId, lastN);
    }

    @DeleteMapping("/history/{sessionId}")
    @Operation(summary = "清空历史消息")
    public void deleteHistory(@PathVariable String sessionId) {
        chatMemory.clear(sessionId);
    }
}
