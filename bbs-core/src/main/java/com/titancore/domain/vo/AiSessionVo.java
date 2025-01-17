package com.titancore.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiSessionVo {
    /**
     * 主键
     */
    private String id;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 创建者Id
     */
    private String creatorId;

    /**
     * 对话条数
     */
    private Integer num;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editedTime;
}
