package com.titancore.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "聊天群响应对象")
public class ChatGroupVo {

    @Schema(description = "群号")
    private String groupId;
    @Schema(description = "状态")
    private boolean status = false;
    @Schema(description = "结果")
    private String message;

}
