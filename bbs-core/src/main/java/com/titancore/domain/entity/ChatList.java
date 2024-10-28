package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.titancore.enums.SourceType;
import lombok.Data;

/**
 * 聊天列表
 * @TableName chat_list
 */
@TableName(value = "chat_list", autoResultMap = true)
@Data
public class ChatList {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话所有者id
     */
    private Long fromId;

    /**
     * 会话目标id
     */
    private Long toId;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 未读消息数量
     */
    private Integer unreadNum;

    /**
     * 最后消息内容
     */
    @TableField(value = "last_msg_content", typeHandler = JacksonTypeHandler.class)
    private ChatMessageContent lastMsgContent;

    /**
     * 类型 标识是系统/群组/用户
     */
    @TableField("`source_type`")
    private SourceType sourceType;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;


}
