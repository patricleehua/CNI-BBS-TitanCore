package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
@EqualsAndHashCode(callSuper = true)
@TableName(value ="ai_message")
@Data
public class AiMessageMedia extends BaseEntity{

    /**
     * 媒体Id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 会话Id
     */
    private Long sessionId;
    /**
     * 媒体类型
     */
    private String type;
    /**
     * 媒体url
     */
    private String url;
    /**
     * 创建时间
     */
    private LocalDateTime localDateTime;

}
