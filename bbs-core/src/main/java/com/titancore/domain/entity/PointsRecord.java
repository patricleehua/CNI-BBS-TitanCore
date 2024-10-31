package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import com.titancore.enums.PointsStatus;
import lombok.Data;

/**
 * 积分记录表
 * @TableName points_record
 */
@TableName(value ="points_record")
@Data
public class PointsRecord{
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分规则ID
     */
    private Long ruleId;

    /**
     * 积分数
     */
    private Integer points;

    /**
     * 积分状态
     */
    @TableField("`status`")
    private PointsStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    private LocalDateTime expirationTime;

    /**
     * 已使用积分数
     */
    private Integer usedPoints;

}