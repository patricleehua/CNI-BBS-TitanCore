package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "PostCommentParam", description = "帖子评论参数传递对象")
public class PostCommentParam extends PageParams {

    @Schema(description = "帖子Id")
    @NonNull
    private String postId;
    @Schema(description = "当前评论人Id(删除使用)")
    private String userId;
    @Schema(description = "此条评论的Id(删除使用)")
    private String postCommentId;
}
