package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AiMessageDTO", description = "用户发送的消息/AI回复的消息")
public class AiMessageDTO {


    /**
     * 会话id
     */
    private String aiSessionId;

    /**
     * 创建者id
     */
    private String creatorId;

    /**
     * 编辑者Id
     */
    private String editorId;

    /**
     * 消息类型(用户/ 助手/ 系统)
     */
    private String type;

    /**
     * 消息内容
     */
    private String textContent;

    /**
     * 媒体内容如图片链接、语音链接
     */
    private List<AiMessageMediaDTO> medias;

    /**
     * 是否使用RAG检索增强
     */
    private boolean enableVectorStore = false;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editedTime;
}
