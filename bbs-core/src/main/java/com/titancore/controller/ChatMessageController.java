package com.titancore.controller;

import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.vo.DMLVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chat/message")
@Slf4j
@Tag(name = "实时通讯-信息模块")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * 发送消息
     * @param chatMessageDTO
     * @return
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息")
    public Response<?> sendMessage(@RequestBody ChatMessageDTO chatMessageDTO) {
        DMLVo DMLVo = chatMessageService.sendMessage(chatMessageDTO);
        return Response.success(DMLVo);
    }

    /**
     * 撤回消息
     *
     * @return
     */


    /**
     * 重新编辑
     *
     * @return
     */


    /**
     * 聊天记录
     *
     * @return
     */


    /**
     * 聊天记录（降序）
     *
     * @return
     */



    /**
     * 发送文件
     *
     * @return
     */


    /**
     * 发送图片
     *
     * @return
     */



    /**
     * 获取文件
     *
     * @return
     */

}
