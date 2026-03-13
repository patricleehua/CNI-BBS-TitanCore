package com.titancore.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.titancore.domain.dto.PermissionDTO;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PermissionParam;
import com.titancore.domain.vo.PermissionTreeVO;
import com.titancore.domain.vo.PermissionVO;
import com.titancore.framework.common.response.Response;
import com.titancore.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/permission")
@Slf4j
@AllArgsConstructor
@Tag(name = "权限管理接口")
@SaCheckRole("superpower_user")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/list")
    @Operation(summary = "分页查询权限列表")
    public Response<PageResult> list(@RequestBody PermissionParam param) {
        PageResult result = permissionService.listPermissions(param);
        return Response.success(result);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取权限树形结构")
    public Response<List<PermissionTreeVO>> tree() {
        List<PermissionTreeVO> tree = permissionService.getPermissionTree();
        return Response.success(tree);
    }

    @GetMapping("/{permissionId}")
    @Operation(summary = "获取权限详情")
    public Response<PermissionVO> getById(@PathVariable Long permissionId) {
        PermissionVO vo = permissionService.getPermissionById(permissionId);
        return Response.success(vo);
    }

    @PostMapping("/create")
    @Operation(summary = "创建权限")
    public Response<Boolean> create(@Valid @RequestBody PermissionDTO dto) {
        boolean result = permissionService.createPermission(dto);
        return Response.success(result);
    }

    @PutMapping("/update")
    @Operation(summary = "更新权限")
    public Response<Boolean> update(@Valid @RequestBody PermissionDTO dto) {
        boolean result = permissionService.updatePermission(dto);
        return Response.success(result);
    }

    @DeleteMapping("/{permissionId}")
    @Operation(summary = "删除权限")
    public Response<Boolean> delete(@PathVariable Long permissionId) {
        boolean result = permissionService.deletePermission(permissionId);
        return Response.success(result);
    }
}
