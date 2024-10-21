package com.titancore.domain.dto;

import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.enums.LevelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "发送消息DTO", description = "发送消息数据传输对象")
public class ChatMessageDTO extends BaseDTO{
    @Schema(description = "发送方ID")
    private String fromId;
    @Schema(description = "目标ID")
    private String toId;
    @Schema(description = "目标源:user,group")
    private String source;
    @Schema(description = "消息级别:system,normal")
    private String level;
    @Schema(description = "消息内容")
    private ChatMessageContent chatMessageContent;
}
