package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;


@Data
@TableName("chat_group_notice")
public class ChatGroupNotice{

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 聊天群id
     */
    @TableField("chat_group_id")
    private Long chatGroupId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 公告内容
     */
    @TableField("notice_content")
    private String noticeContent;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
