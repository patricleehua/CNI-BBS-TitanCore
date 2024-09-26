package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "标签DTO", description = "标签数据传输对象")
public class TagDTO {

    @Schema(description = "标签名")
    private String tagName;
    @Schema(description = "标签图片url")
    private String tagUrl;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "创建者Id")
    private String userId;

}
