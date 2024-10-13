package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "板块DTO", description = "板块数据传输对象")
public class CategoryDTO extends BaseDTO{

    @Schema(description = "板块Id")
    private String categoryId;
    @Schema(description = "板块名")
    @NonNull
    private String categoryName;
    @Schema(description = "板块图片url")
    @NonNull
    private String categoryUrl;
    @Schema(description = "描述")
    @NonNull
    private String description;
    @Schema(description = "创建者Id")
    @NonNull
    private String userId;

}
