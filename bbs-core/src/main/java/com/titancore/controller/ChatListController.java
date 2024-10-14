package com.titancore.controller;

import com.titancore.domain.dto.ChatListDTO;
import com.titancore.domain.param.ChatListParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatListVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ChatListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/chat-list")
@Tag(name = "实时通讯-聊天列表模块")
public class ChatListController {
    @Autowired
    private ChatListService chatListService;

    /**
     * 获取聊天列表
     * @param chatListParam
     * @return
     */
    @PostMapping("/list")
    @Operation(summary = "获取聊天列表")
    public Response<?> getChatList(@RequestBody ChatListParam chatListParam) {
        PageResult page = chatListService.queryChatList(chatListParam);
        return Response.success(page);
    }

    /**
     * 创建聊天会话
     * @param chatListDTO
     * @return
     */
    @PostMapping("/createChatList")
    @Operation(summary = "创建聊天会话")
    public Response<?> createChatList(@RequestBody ChatListDTO chatListDTO) {
        ChatListVo result = chatListService.createChatList(chatListDTO);
        return Response.success(result);
    }

    /**
     * 删除聊天会话
     * @param chatListDTO
     * @return
     */
    @PostMapping("/deleteChatList")
    @Operation(summary = "删除聊天会话")
    public Response<?> deleteChatList(@RequestBody ChatListDTO chatListDTO) {
        ChatListVo result = chatListService.deleteChatList(chatListDTO);
        return Response.success(result);
    }

}
