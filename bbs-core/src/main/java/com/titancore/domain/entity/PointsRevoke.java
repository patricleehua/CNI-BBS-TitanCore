package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分撤销记录表
 * @TableName points_revoke
 */
@TableName(value ="points_revoke")
@Data
public class PointsRevoke{
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 使用记录ID
     */
    private Long usageId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 撤销的积分数
     */
    private Integer revokePoints;

    /**
     * 撤销时间
     */
    private LocalDateTime revokeTime;

    /**
     * 撤销原因
     */
    private String reason;

}