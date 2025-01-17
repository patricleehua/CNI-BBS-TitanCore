package com.titancore.controller;

import com.titancore.domain.dto.AiSessionDTO;
import com.titancore.domain.vo.AiSessionVo;
import com.titancore.service.AiSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/aiSession")
@RestController
@Slf4j
@Tag(name = "AI本地会话管理")
public class AiSessionController {
    private final AiSessionService aiSessionService;
    public AiSessionController(AiSessionService aiSessionService) {
        this.aiSessionService = aiSessionService;
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "根据会话id查询会话信息")
    public AiSessionVo findById(@PathVariable String sessionId) {
        return aiSessionService.findSessionBySessionId(sessionId);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询当前登录用户的会话" )
    public List<AiSessionVo> findSessionsByUserId(@PathVariable String userId) {
        return aiSessionService.findSessionsByUserId(userId);
    }

    @PostMapping("/user/createSession")
    @Operation(summary = "创建会话" )
    public boolean createSession(@RequestBody AiSessionDTO aiSessionDto){
        return aiSessionService.createSession(aiSessionDto);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "删除会话")
    public boolean deleteSession(@PathVariable String id) {
        //todo 删除聊天对话
        return aiSessionService.removeById(id);
    }
}
