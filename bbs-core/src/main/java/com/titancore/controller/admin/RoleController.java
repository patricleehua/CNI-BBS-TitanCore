package com.titancore.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.titancore.domain.dto.AssignPermissionDTO;
import com.titancore.domain.dto.RoleDTO;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.RoleParam;
import com.titancore.domain.vo.RoleDetailVO;
import com.titancore.domain.vo.RoleVO;
import com.titancore.framework.common.response.Response;
import com.titancore.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/role")
@Slf4j
@AllArgsConstructor
@Tag(name = "角色管理接口")
@SaCheckRole("superpower_user")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/list")
    @Operation(summary = "分页查询角色列表")
    public Response<PageResult> list(@RequestBody RoleParam param) {
        PageResult result = roleService.listRoles(param);
        return Response.success(result);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有启用的角色(下拉选择用)")
    public Response<List<RoleVO>> listAll() {
        List<RoleVO> roles = roleService.listAllRoles();
        return Response.success(roles);
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "获取角色详情(含权限)")
    public Response<RoleDetailVO> getById(@PathVariable Long roleId) {
        RoleDetailVO vo = roleService.getRoleById(roleId);
        return Response.success(vo);
    }

    @PostMapping("/create")
    @Operation(summary = "创建角色")
    public Response<Boolean> create(@Valid @RequestBody RoleDTO dto) {
        boolean result = roleService.createRole(dto);
        return Response.success(result);
    }

    @PutMapping("/update")
    @Operation(summary = "更新角色")
    public Response<Boolean> update(@Valid @RequestBody RoleDTO dto) {
        boolean result = roleService.updateRole(dto);
        return Response.success(result);
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色")
    public Response<Boolean> delete(@PathVariable Long roleId) {
        boolean result = roleService.deleteRole(roleId);
        return Response.success(result);
    }

    @PutMapping("/status/{roleId}")
    @Operation(summary = "切换角色状态")
    public Response<Boolean> toggleStatus(@PathVariable Long roleId) {
        boolean result = roleService.toggleStatus(roleId);
        return Response.success(result);
    }

    @PostMapping("/assignPermission")
    @Operation(summary = "为角色分配权限")
    public Response<Boolean> assignPermission(@Valid @RequestBody AssignPermissionDTO dto) {
        boolean result = roleService.assignPermission(dto);
        return Response.success(result);
    }
}
