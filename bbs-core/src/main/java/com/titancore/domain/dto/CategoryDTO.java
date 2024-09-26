package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "分类DTO", description = "分类数据传输对象")
public class CategoryDTO {

    @Schema(description = "分类名")
    private String categoryName;
    @Schema(description = "分类图片url")
    private String categoryUrl;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "创建者Id")
    private String userId;

}
