package com.titancore.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "聊天列表操纵结果返回对象")
public class ChatListDmlVo {

    @Schema(description = "聊天对话Id标识")
    private String chatListId;
    @Schema(description = "状态")
    private boolean status = false;
    @Schema(description = "结果")
    private String message;

}
