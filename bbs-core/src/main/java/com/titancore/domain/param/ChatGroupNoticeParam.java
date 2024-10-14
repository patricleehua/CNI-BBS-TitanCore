package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "群组-公告参数", description = "群组-公告参数对象")
public class ChatGroupNoticeParam extends PageParams {
    @Schema(description = "群号")
    private String groupId;
}
