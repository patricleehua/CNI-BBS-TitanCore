package com.titancore.controller;

import com.titancore.domain.param.ChatMemberParam;
import com.titancore.domain.param.PageResult;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ChatGroupMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/chat-group-member")
@Tag(name = "实时通讯-聊天群-成员关系模块")
public class ChatGroupMemberController {

    @Autowired
    private ChatGroupMemberService chatGroupMemberService;

    @PostMapping("/list")
    @Operation(summary = "根据群号分组查询群成员列表")
    public Response<?> memberList(@RequestBody ChatMemberParam chatMemberParam) {
        PageResult page = chatGroupMemberService.memberList(chatMemberParam);
        return Response.success(page);
    }

}
