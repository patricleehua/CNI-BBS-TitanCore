package com.titancore.domain.vo;


import lombok.Data;
import java.time.LocalDateTime;


@Data
public class PointsRuleVo {
    /**
     * 主键
     */
    private String id;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 积分规则唯一标识
     */
    private String uniqueKey;

    /**
     * 积分数
     */
    private Integer points;

    /**
     * 行为类型
     */
    private String behaviorType;

    /**
     * 是否启用
     */
    private Integer isActive;

    /**
     * 有效期（天）
     */
    private Integer validityPeriod;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
