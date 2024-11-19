package com.titancore.controller;

import com.titancore.domain.dto.ChatListDTO;
import com.titancore.domain.dto.SetTopForChatListDTO;
import com.titancore.domain.param.ChatListParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatListDmlVo;
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
        ChatListDmlVo result = chatListService.createChatList(chatListDTO);
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
        ChatListDmlVo result = chatListService.deleteChatList(chatListDTO);
        return Response.success(result);
    }

    /**
     * 置顶会话
     * @param setTopChatListDTO
     * @return
     */
    @PostMapping("/setTopChatList")
    @Operation(summary = "置顶会话")
    public Response<?> setTopChatList(@RequestBody SetTopForChatListDTO setTopChatListDTO) {
        ChatListDmlVo result = chatListService.setTopChatList(setTopChatListDTO);
        return Response.success(result);
    }

    /**
     * 消息已读
     * @param fromId
     * @param toId
     * @return
     */
    @GetMapping("/read/{fromId}/{toId}")
    @Operation(summary = "消息已读")
    public Response<?> messageRead(@PathVariable String fromId,@PathVariable String toId) {
        ChatListDmlVo result = chatListService.messageRead(fromId, toId);
        return Response.success(result);
    }

    /**
     * 全部已读
     * @param userId
     * @return
     */
    @GetMapping("/read/all")
    @Operation(summary = "全部已读")
    public Response<?> messageReadAll(@RequestParam String userId) {
        ChatListDmlVo result = chatListService.messageReadAll(userId);
        return Response.success(result);
    }

    /**
     * 获取聊天列表详细信息
     * @param chatListDTO
     * @return
     */
    @PostMapping("/detail")
    @Operation(summary = "获取聊天列表详细信息")
    public Response<?> detailChatList(@RequestBody ChatListDTO chatListDTO) {
        ChatListVo chatListVo = chatListService.detailChartList(chatListDTO);
        return Response.success(chatListVo);
    }
}
