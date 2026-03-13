package com.titancore.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "RoleDetailVO", description = "角色详情(含权限)VO")
public class RoleDetailVO extends RoleVO {

    @Schema(description = "拥有的权限列表")
    private List<PermissionVO> permissions;
}
