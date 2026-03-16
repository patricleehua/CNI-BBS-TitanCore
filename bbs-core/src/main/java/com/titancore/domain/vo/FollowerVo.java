package com.titancore.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "FollowerVo", description = "关注列表用户信息")
public class FollowerVo {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 对外公开的用户名（唯一标识，如 @username）
     */
    @Schema(description = "对外公开的用户名")
    private String publicUsername;

    /**
     * 用户昵称（显示名称）
     */
    @Schema(description = "用户昵称")
    private String nickName;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String avatar;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介")
    private String bio;

    /**
     * 备注（对关注者的备注）
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 分组id
     */
    @Schema(description = "分组id")
    private Long groupId;

    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String groupName;

    /**
     * 关注数
     */
    @Schema(description = "关注数")
    private Long followingCount;

    /**
     * 粉丝数
     */
    @Schema(description = "粉丝数")
    private Long followersCount;

    /**
     * 帖子数量
     */
    @Schema(description = "帖子数量")
    private Long postsCount;

    /**
     * 当前登录用户对该用户的关注状态
     */
    @Schema(description = "当前用户对该用户的关注状态：NONE-未关注，CONFIRMED-已关注，PENDING-待确认，REJECTED-被拒绝，MUTUAL-互相关注")
    private String followStatus;

    /**
     * 是否为互相关注
     */
    @Schema(description = "是否互相关注")
    private Boolean isMutualFollow;

    /**
     * 是否为公开账户
     */
    @Schema(description = "是否为公开账户")
    private Boolean isPublicAccount;

    /**
     * 拉黑状态
     */
    @Schema(description = "是否被拉黑")
    private Boolean isBlocked;

    /**
     * 关注创建时间
     */
    @Schema(description = "关注创建时间")
    private LocalDateTime createTime;

}
