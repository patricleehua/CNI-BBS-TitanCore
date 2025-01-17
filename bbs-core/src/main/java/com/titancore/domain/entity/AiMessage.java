package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.titancore.enums.AiMessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天记录表
 * @TableName ai_message
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="ai_message")
@Data
public class AiMessage extends BaseEntity{
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话id
     */
    private Long aiSessionId;

    /**
     * 创建者Id
     */
    private Long creatorId;

    /**
     * 编辑者Id
     */
    private Long editorId;

    /**
     * 消息类型(用户/ 助手/ 系统)
     */
    private AiMessageType type;

    /**
     * 消息内容
     */
    private String textContent;

    /**
     * 媒体内容如图片链接、语音链接
     */
    @TableField(value = "medias", typeHandler = JacksonTypeHandler.class)
    private List<AiMessageMedia> medias;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editedTime;

}