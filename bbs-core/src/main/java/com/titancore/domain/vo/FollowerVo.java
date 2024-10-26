package com.titancore.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowerVo {

    /**
     * 关注者id/被关注者id
     */
    private Long followerId;

    /**
     * 关注者名称/被关注者名称
     */

    private String followerName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 分组id
     */
    private Long groupId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 拉黑状态
     */
    private Integer isBacked;

    /**
     * 关注状态
     */
    private String followStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
