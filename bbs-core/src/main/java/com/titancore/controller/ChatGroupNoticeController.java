package com.titancore.controller;

import com.titancore.domain.dto.ChatNoticeDTO;
import com.titancore.domain.param.ChatGroupNoticeParam;
import com.titancore.domain.param.PageResult;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ChatGroupNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/group-notice")
@Tag(name = "实时通讯-聊天群-公告模块")
public class ChatGroupNoticeController {

    @Autowired
    private ChatGroupNoticeService chatGroupNoticeService;

    /**
     * 创建/编辑群公告
     *
     * @return
     */
    @PostMapping("/create")
    @Operation(summary = "创建/编辑群公告")
    public Response<?> createNotice(@RequestBody ChatNoticeDTO chatNoticeDTO) {
        boolean result = chatGroupNoticeService.createNotice(chatNoticeDTO);
        return Response.success(result);
    }

    /**
     * 群公告列表
     *
     * @return
     */
    @PostMapping("/list")
    @Operation(summary = "获取指定群公告列表（含历史）")
    public Response<?>  noticeList(@RequestBody ChatGroupNoticeParam chatGroupNoticeParam) {
        PageResult result = chatGroupNoticeService.noticeList(chatGroupNoticeParam);
        return Response.success(result);
    }

    /**
     * 删除群公告
     *
     * @return
     */
    @DeleteMapping("/delete/{noticeId}")
    @Operation(summary = "删除指定群公告")
    public  Response<?> deleteNotice( @PathVariable("noticeId") @Parameter(description = "公告ID") String noticeId) {
        boolean result = chatGroupNoticeService.deleteNotice(noticeId);
        return Response.success(result);
    }

}
