package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PostCommentsDTO", description = "帖子评论数据传输对象")
public class PostCommentsDTO extends BaseDTO{

    @Schema(description = "帖子Id")
    @NonNull
    private String postId;
    @Schema(description = "父评论Id 如果没有父评论则为0")
    @NonNull
    private String parentId;
    @Schema(description = "当前评论者Id")
    @NonNull
    private String userId;
    @Schema(description = "对谁评论 如果是父评论则为作者，否则为被评论用户")
    @NonNull
    private String toUserId;
    @Schema(description = "被回复那个评论的Id 如果自己是父评论则为0")
    @NonNull
    private String replyCommentId;
    @Schema(description = "帖子内容")
    @NonNull
    private String content;
}
