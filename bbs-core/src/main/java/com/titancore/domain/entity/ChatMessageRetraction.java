package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

/**
 * 消息撤回内容表
 * @TableName chat_message_retraction
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="chat_message_retraction", autoResultMap = true)
@Data
public class ChatMessageRetraction extends BaseEntity{

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 消息id
     */
    private Long msgId;

    /**
     * 撤回消息内容
     */
    @TableField(value = "msg_content",typeHandler = JacksonTypeHandler.class)
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