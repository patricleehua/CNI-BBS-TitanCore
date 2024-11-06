package com.titancore.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PostDTO", description = "帖子数据传输对象")
public class PostDTO  extends BaseDTO{

    @Schema(description = "帖子Id")
    private String id;
    @Schema(description = "作者Id")
    @NonNull
    private String authorId;
    @Schema(description = "文章标题")
    @NonNull
    private String title;
    @Schema(description = "帖子描述")
    private String summary;
    @Schema(description = "帖子内容")
    private String content;
    @Schema(description = "帖子内容HTML格式")
    private String contentHtml;
    @Schema(description = "帖子分类(板块A/板块B)")
    @NonNull
    private String categoryId;
    @Schema(description = "帖子类型(图文0/视频1)")
    @NonNull
    private String type;
    @Schema(description = "帖子标签Id列表")
    private List<String> tagIds;
}
