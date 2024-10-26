package com.titancore.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.titancore.enums.FollowStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value ="follow")
@Data
public class Follow extends BaseEntity{
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id/被关注者
     */
    private Long userId;

    /**
     * 关注者id
     */
    private Long followerId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 分组id
     */
    private Long groupId;

    /**
     * 我是否拉黑了他0/1
     */
    private Integer isBlocked;


    /**
     * 关注状态
     */
    @TableField("`status`")
    private FollowStatus followStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}