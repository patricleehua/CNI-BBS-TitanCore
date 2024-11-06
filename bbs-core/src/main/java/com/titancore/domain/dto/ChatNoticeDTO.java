package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatNoticeDTO", description = "聊天群通知数据传输对象")
public class ChatNoticeDTO  extends BaseDTO{
    @Schema(description = "通知ID，如果为空则为插入否则为编辑")
    private String noticeId;
    @Schema(description = "群号/ID")
    private String groupId;
    @Schema(description = "群主Id")
    private String ownerUserId;
    @Schema(description = "通知内容")
    private String noticeContent;

}
