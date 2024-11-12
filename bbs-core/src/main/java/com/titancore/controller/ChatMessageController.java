package com.titancore.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.dto.ChatMessageFileDTO;
import com.titancore.domain.dto.ReeditDTO;
import com.titancore.domain.dto.RetractionDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.domain.param.ChatMessageParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatMessageRetractionVo;
import com.titancore.domain.vo.DMLVo;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.response.Response;
import com.titancore.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;


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
     * 发送文件(先存消息在调用发送)
     * @param file 文件
     * @param userId 用户ID
     * @param msgId 预存消息ID
     * @return
     */
    @PostMapping("/send/file")
    @Operation(summary = "发送文件")
    public Response<?> sendFile(MultipartFile file, String userId, String msgId) {
        String url = chatMessageService.sendFileOnMsgId(file,userId,msgId);
        return Response.success(url);
    }

    /**
     * 发送媒体(先存消息在调用发送)
     * @param file
     * @param userId
     * @param msgId
     * @return
     */
    @PostMapping("/send/media")
    @Operation(summary = "发送媒体")
    public Response<?> sendImage(MultipartFile file, String userId, String msgId) {
        String url = chatMessageService.sendMediaOnMsgId(file,userId,msgId);
        return Response.success(url);
    }


    /**
     * 获取文件
     * @param userId 发送者/接收者
     * @param msgId 消息Id
     * @param range 代表发送范围获取数据的请求  格式： bytes=0-500   bytes=501-  bytes=0-100,101-200
     * @return
     */
    @GetMapping("/get/file")
    @Operation(summary = "获取文件")
    public ResponseEntity<StreamingResponseBody> getFile(
            @RequestParam("userId") String userId,
            @RequestParam("msgId") String msgId,
            @RequestHeader(value = "Range", required = false) String range) {
        // 获取消息内容，包括文件路径、名称和大小信息
        ChatMessageContent fileMsgContent = chatMessageService.getFileMsgContent(new ChatMessageFileDTO(userId, msgId));
        JSONObject fileInfo = JSON.parseObject(fileMsgContent.getContent());
        String fileName = fileInfo.getString("fileName");
        long fileSize = fileInfo.getLong("fileSize");
        String filePath = fileInfo.getString("filePath");
        long start;
        long end = fileSize - 1;

        // 解析 Range 头部信息，实现断点续传
        if (range != null && range.startsWith("bytes=")) {
            String[] ranges = range.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            }
        } else {
            start = 0;
        }

        // 确保请求的范围有效
        if (start > end || end >= fileSize) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                    .build();
        }
        // 实际要传输的数据长度
        long contentLength = end - start + 1;

        /**
         * 2024/11/12 patricLee
         * 创建 StreamingResponseBody 以流式传输文件内容 作用L:
         * StreamingResponseBody 是 Spring MVC 中提供的一种响应机制，用于处理流式数据的传输。
         * 它允许我们在客户端请求时动态地将数据写入响应流，而无需将整个响应缓存在服务器内存中（这很关键）。
         * 这种机制非常适合处理大文件传输，因为 StreamingResponseBody 会自动处理响应的分块传输（chunked transfer encoding），而且对客户端的中断异常有内置的支持。
         * 客户端中断时会自动停止写入，避免不必要的资源占用（其二）
         */
        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = chatMessageService.getFileToInputStreamByFilePath(filePath, start, contentLength)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
            } catch (ClientAbortException e) {
                log.warn("客户端连接中断: {}", e.getMessage());
            } catch (Exception e) {
                log.error("文件流写入失败, userId: {}, msgId: {}", userId, msgId, e);
                throw new BizException(ResponseCodeEnum.FILE_DOWNLOAD_IS_FAIL);
            }
        };
        // 返回文件的分块内容，包含适当的头部信息
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize)
                .body(stream);
    }

    /**
     * 获取媒体
     * @param userId
     * @param msgId
     * @return
     */
    @GetMapping("/get/media")
    @Operation(summary = "获取媒体")
    public Response<?> getMedia(@RequestParam("userId") String userId, @RequestParam("msgId") String msgId) {
        String url = chatMessageService.getMedia(userId, msgId);
        return Response.success(url);
    }
}
