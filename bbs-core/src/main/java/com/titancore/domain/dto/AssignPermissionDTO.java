package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(name = "AssignPermissionDTO", description = "角色权限分配DTO")
public class AssignPermissionDTO {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
