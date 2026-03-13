package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(name = "RoleDTO", description = "角色创建/更新DTO")
public class RoleDTO {

    @Schema(description = "角色ID(更新时必填)")
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    @Schema(description = "角色唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleKey;

    @Schema(description = "状态(0：启用 1：禁用)")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;
}
