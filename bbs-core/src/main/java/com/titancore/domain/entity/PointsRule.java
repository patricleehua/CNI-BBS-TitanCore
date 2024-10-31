package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分规则表
 * @TableName points_rule
 */
@TableName(value ="points_rule")
@Data
public class PointsRule{
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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