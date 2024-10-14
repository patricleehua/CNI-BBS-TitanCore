package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "聊天群参数", description = "聊天群参数对象")
public class ChatGroupParam extends PageParams {

    @Schema(description = "群号")
    private String id;
    @Schema(description = "创建者")
    private String userId;
    @Schema(description = "群名")
    private String groupName;

}
