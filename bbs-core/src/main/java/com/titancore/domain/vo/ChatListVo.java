package com.titancore.domain.vo;


import com.titancore.domain.entity.ChatMessageContent;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ChatListVo {
    /**
     * 主键
     */
    private String id;

    /**
     * 会话所有者id
     */
    private String fromId;

    /**
     * 会话目标id
     */
    private String toId;

    /**
     * 会话目标名称/群名/用户名/好友备注
     */
    private String toName;

    /**
     * 会话目标头像
     */
    private String toPortrait;

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
    private ChatMessageContent lastMsgContent;

    /**
     * 类型 标识是系统/群组/用户
     */
    private String sourceType;

    /**
     * 状态
     */
    private String status;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
