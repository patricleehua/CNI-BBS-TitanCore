package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "帖子传递参数", description = "帖子传递参数对象")
public class PostParam extends PageParams {
    @Schema(name = "帖子id")
    private String postId;
    @Schema(name = "帖子归属分类Id")
    private String categoryId;
    @Schema(name = "帖子携带的标签id列表")
    private List<String> tagIds;
}
