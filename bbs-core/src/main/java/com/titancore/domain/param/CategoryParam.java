package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "CategoryParam", description = "帖子分类参数对象")
public class CategoryParam extends PageParams {
    @Schema(description = "分类名称")
    private String categoryName;

}
