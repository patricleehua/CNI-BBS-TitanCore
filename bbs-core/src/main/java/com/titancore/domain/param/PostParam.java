package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "PostParam", description = "帖子传递参数对象")
public class PostParam extends PageParams {
    @Schema(description = "帖子id")
    private String postId;
    @Schema(description = "帖子归属分类Id")
    private String categoryId;
    @Schema(description = "帖子携带的标签id列表")
    private List<String> tagIds;
    @Schema(description = "当前登入用户id(主要给关注功能提供)")
    private String userId;
}
