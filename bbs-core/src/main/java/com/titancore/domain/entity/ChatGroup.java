package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "chat_group", autoResultMap = true)
public class ChatGroup{

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 群主id
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

    /**
     * 群头像
     */
    @TableField("portrait")
    private String portrait;

    /**
     * 群名名称
     */
    @TableField("name")
    private String name;

    /**
     * 群公告
     */
    @TableField(value = "notice", typeHandler = JacksonTypeHandler.class)
    private ChatGroupNotice notice;

    /**
     * 成员数
     */
    @TableField("member_num")
    private Integer memberNum;
    /**
     * 是否公开
     */
    @TableField("is_open")
    private Integer isOpen;

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
