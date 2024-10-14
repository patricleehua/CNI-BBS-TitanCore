package com.titancore.controller;


import com.titancore.domain.dto.ChatGroupDTO;
import com.titancore.domain.param.ChatGroupParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatGroupVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ChatGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chat/group")
@Tag(name = "实时通讯-聊天群模块")
public class ChatGroupController {
    @Autowired
    private ChatGroupService chatGroupService;

    /**
     * 群聊列表
     */
    @PostMapping("/list")
    @Operation(summary = "聊天群组列表")
    public Response<?> chatGroupList(@RequestBody ChatGroupParam chatGroupParam) {
        PageResult result = chatGroupService.chatGroupList(chatGroupParam);
        return Response.success(result);
    }

    /**
     * 创建群聊
     */
    @PostMapping("/create")
    @Operation(summary = "创建群聊")
    public Response<?> createChatGroup(@RequestBody ChatGroupDTO chatGroupDTO) {
        ChatGroupVo chatGroupVo = chatGroupService.createChatGroup(chatGroupDTO);
        return Response.success(chatGroupVo);
    }

    /**
     * 更新群信息
     */


    /**
     * 更新群信息(群名称)
     */


    /**
     * 成员邀请
     */

    /**
     * 退出群聊
     */



    /**
     * 踢出群聊
     */


    /**
     * 解散群聊
     */
    @PostMapping("/dissolve")
    @Operation(summary = "解散群聊")
    public Response<?> dissolveChatGroup(@RequestBody ChatGroupDTO chatGroupDTO) {
        ChatGroupVo chatGroupVo = chatGroupService.dissolveChatGroup(chatGroupDTO);
        return Response.success(chatGroupVo);
    }

    /**
     * 转让群聊
     */


    /**
     * 群详情
     */


    /**
     * 更新群头像
     */

}
