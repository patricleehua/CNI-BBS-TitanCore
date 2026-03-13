package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "PermissionParam", description = "权限分页查询参数")
public class PermissionParam extends PageParams {

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限标识")
    private String permissionKey;

    @Schema(description = "类型(1：目录 2：菜单 3：按钮)")
    private Integer type;

    @Schema(description = "状态(0：启用；1：禁用)")
    private Integer status;
}
