package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "聊天列表DTO", description = "聊天列表数据传输对象")
public class ChatListDTO extends BaseDTO{
    @Schema(description = "会话ID")
    private String chatListId;
    @Schema(description = "会话所有者ID")
    private String fromId;
    @Schema(description = "目标用户/聊天群ID")
    private String toId;
    @Schema(description = "目标源:user,group")
    private String source;

}
