package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会话记录表
 * @TableName ai_session
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="ai_session")
@Data
public class AiSession extends BaseEntity{
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 
     */
    private Long creatorId;

    /**
     * 
     */
    private Long editorId;

    /**
     * 是否为临时会话
     */
    private Integer isTemporary;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 临时会话过期时间
     */
    private LocalDateTime expiryTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editedTime;
}