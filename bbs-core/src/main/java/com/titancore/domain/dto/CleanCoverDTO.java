package com.titancore.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CleanCoverDTO", description = "帖子封面数据删除对象")
public class CleanCoverDTO extends BaseDTO{

    @Schema(description = "帖子Id")
    private String postId;
    @Schema(description = "作者Id")
    @NonNull
    private String authorId;
    @Schema(description = "帖子封面Url")
    @NonNull
    private String coverUrl;

}
