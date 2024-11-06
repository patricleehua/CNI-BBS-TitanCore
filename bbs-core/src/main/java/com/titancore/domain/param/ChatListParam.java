package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatListParam", description = "聊天列表参数对象")
public class ChatListParam extends PageParams {

    @Schema(description = "会话所有者ID")
    private String fromId;

}
