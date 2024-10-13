package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "标签DTO", description = "标签数据传输对象")
public class TagDTO extends BaseDTO{

    @Schema(description = "标签Id")
    private String tagId;
    @Schema(description = "标签名")
    @NonNull
    private String tagName;
    @Schema(description = "标签图片url")
    @NonNull
    private String tagUrl;
    @Schema(description = "描述")
    @NonNull
    private String description;
    @Schema(description = "所属板块Id")
    private String categoryId;
    @Schema(description = "创建者Id")
    @NonNull
    private String userId;

}
