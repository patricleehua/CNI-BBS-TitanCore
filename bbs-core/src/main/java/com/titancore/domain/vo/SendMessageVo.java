package com.titancore.domain.vo;

import com.titancore.domain.entity.ChatMessageContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "发送消息响应对象")
public class SendMessageVo {
    //目标用户
    private String toUserId;
    //目标源
    private String source; //目标源:user,group
    //消息内容
    private ChatMessageContent chatMessageContent;
}
