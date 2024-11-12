package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatMessageFileDTO", description = "消息文件数据传输对象")
public class ChatMessageFileDTO extends BaseDTO{
    @Schema(description = "用户ID")
    @NonNull
    private String userId;
    @Schema(description = "消息ID")
    @NonNull
    private String msgId;
}
