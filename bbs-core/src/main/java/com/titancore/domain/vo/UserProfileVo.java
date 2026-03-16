package com.titancore.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户资料视图对象
 */
@Data
@Schema(name = "UserProfileVo", description = "用户资料视图对象")
public class UserProfileVo implements Serializable {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "对外公开的用户名")
    private String publicUsername;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户简介")
    private String bio;

    @Schema(description = "关注数")
    private Long followingCount;

    @Schema(description = "粉丝数")
    private Long followersCount;

    @Schema(description = "当前用户对该用户的关注状态")
    private String followStatus;

    @Schema(description = "用户声望/积分")
    private Long reputation;

    @Schema(description = "帖子数量")
    private Long postsCount;

    @Schema(description = "评论数量")
    private Long commentsCount;
}
