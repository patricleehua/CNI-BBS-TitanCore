package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 积分使用记录表
 * @TableName points_usage
 */
@TableName(value ="points_usage")
@Data
public class PointsUsage{
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
     * 总共使用的积分数
     */
    private Integer totalPointsUsed;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * 使用描述
     */
    private String description;

    /**
     * 使用的积分明细
     */
    private String usedDetails;

    /**
     * 是否已撤销 0/1 是/否
     */
    private Integer isRevoked;

}