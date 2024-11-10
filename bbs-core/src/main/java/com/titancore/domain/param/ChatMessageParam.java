package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatMessageParam", description = "聊天信息参数对象")
public class ChatMessageParam extends PageParams {

    @Schema(description = "会话所有者ID")
    private String fromId;

    @Schema(description = "会话目标ID")
    private String targetId;

    @Schema(description = "是否倒叙（0是/1否）")
    private String isDesc;
}
