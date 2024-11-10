package com.titancore.controller;

import cn.hutool.json.JSONObject;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.dto.ReeditDTO;
import com.titancore.domain.dto.RetractionDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.domain.param.ChatMessageParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatMessageRetractionVo;
import com.titancore.domain.vo.DMLVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


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
     * @param retractionDTO
     * @return
     */
    @PostMapping("/retraction")
    @Operation(summary = "撤回消息")
    public Response<?> retractionMsg(@RequestBody RetractionDTO retractionDTO) {
        ChatMessage result = chatMessageService.retractionMessage(retractionDTO);
        return Response.success(result);
    }

    /**
     * 重新编辑 (返回撤回消息信息)
     * @param reeditDTO
     * @return
     */
    @PostMapping("/reedit")
    @Operation(summary = "重新编辑")
    public Response<?> reeditMessage(@RequestBody ReeditDTO reeditDTO) {
        ChatMessageRetractionVo result = chatMessageService.reeditMessage(reeditDTO);
        return Response.success(result);
    }

    /**
     * 历史聊天记录
     * @param chatMessageParam
     * @return
     */
    @PostMapping("/record")
    @Operation(summary = "历史聊天记录")
    public Response<?> messageRecord(@RequestBody ChatMessageParam chatMessageParam) {
        PageResult result = chatMessageService.messageRecord(chatMessageParam);
        return Response.success(result);
    }

    /**
     * 发送文件
     * @param file 文件
     * @param userId 用户ID
     * @param msgId 预存消息ID
     * @return
     */
    @PostMapping("/send/file")
    @Operation(summary = "历史聊天记录")
    public Response<?> sendFile(MultipartFile file, String userId, String msgId) {
        String url = chatMessageService.sendFileOnMsgId(file,userId,msgId);
        return Response.success(url);
    }

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
