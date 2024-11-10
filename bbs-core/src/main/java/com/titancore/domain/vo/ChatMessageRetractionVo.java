package com.titancore.domain.vo;

import com.titancore.domain.entity.ChatMessageContent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageRetractionVo {
    /**
     * 主键
     */
    private String id;

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 撤回消息内容
     */
    private ChatMessageContent msgContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
