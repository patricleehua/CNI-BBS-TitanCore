package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(name = "PermissionDTO", description = "权限创建/更新DTO")
public class PermissionDTO {

    @Schema(description = "权限ID(更新时必填)")
    private Long id;

    @Schema(description = "父ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "类型(1：目录 2：菜单 3：按钮)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "菜单路由")
    private String menuUrl;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "排序")
    private Integer sort;

    @NotBlank(message = "权限标识不能为空")
    @Schema(description = "权限标识(格式: 模块:资源:操作)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String permissionKey;

    @Schema(description = "状态(0：启用；1：禁用)")
    private Integer status;
}
