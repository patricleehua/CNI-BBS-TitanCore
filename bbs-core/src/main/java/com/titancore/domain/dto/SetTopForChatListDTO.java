package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "SetTopForChatListDTO", description = "聊天列表置顶数据传输对象")
public class SetTopForChatListDTO extends BaseDTO{

    @Schema(description = "会话所有者ID")
    private String fromId;
    @Schema(description = "会话ID")
    private String chatListId;
    @Schema(description = "是否置顶")
    private boolean isTop = false;

}
