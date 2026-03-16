package com.titancore.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户活动视图对象（帖子或评论）
 */
@Data
@Schema(name = "UserActivityVo", description = "用户活动视图对象")
public class UserActivityVo implements Serializable {

    @Schema(description = "活动ID")
    private String id;

    @Schema(description = "活动类型：POST-帖子，COMMENT-评论")
    private String type;

    @Schema(description = "标题（帖子标题或评论所在帖子标题）")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "关联帖子ID")
    private String postId;

    @Schema(description = "点赞数")
    private Integer likesCount;

    @Schema(description = "回复数/评论数")
    private Integer replyCount;
}
