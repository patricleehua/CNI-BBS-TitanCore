package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.titancore.enums.LevelType;
import com.titancore.enums.MessageType;
import com.titancore.enums.SourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "chat_message", autoResultMap = true)
public class ChatMessage extends BaseEntity{

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 消息发送方id
     */
    @TableField("from_id")
    private Long fromId;

    /**
     * 消息接受方id
     */
    @TableField("to_id")
    private Long toId;

    /**
     * 消息类型 (text,image,video,audio)
     */
    @TableField("`message_type`")
    private MessageType type;

    /**
     * 消息类型 (message,notify,media)
     */
    @TableField("`level`")
    private LevelType levelType;

    /**
     * 消息内容
     */
    @TableField(value = "msg_content", typeHandler = JacksonTypeHandler.class)
    private ChatMessageContent ChatMessageContent;

    /**
     * 是否显示时间
     */
    @TableField("is_show_time")
    private Boolean isShowTime;

    /**
     * 消息状态
     */
    @TableField("status")
    private int status;

    /**
     * 消息来源 (user,system,group)
     */
    @TableField("`source_type`")
    private SourceType sourceType;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
