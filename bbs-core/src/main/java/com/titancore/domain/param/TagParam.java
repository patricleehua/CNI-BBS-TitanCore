package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "帖子标签参数对象", description = "帖子标签参数对象")
public class TagParam extends PageParams {
    @Schema(description = "帖子名称")
    private String tagName;
    @Schema(description = "所属板块ID")
    private String categoryId;

}
