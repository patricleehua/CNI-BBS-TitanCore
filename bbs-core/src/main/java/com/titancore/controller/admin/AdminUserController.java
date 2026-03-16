package com.titancore.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.titancore.domain.dto.AdminUserCreateDTO;
import com.titancore.domain.dto.AdminUserUpdateDTO;
import com.titancore.domain.dto.AssignRoleDTO;
import com.titancore.domain.param.AdminUserParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.AdminUserStatisticsVO;
import com.titancore.domain.vo.AdminUserVO;
import com.titancore.framework.common.response.Response;
import com.titancore.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/user")
@Slf4j
@AllArgsConstructor
@Tag(name = "管理员用户管理接口")
@SaCheckRole("superpower_user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/statistics")
    @Operation(summary = "获取用户统计数据")
    public Response<AdminUserStatisticsVO> getStatistics() {
        AdminUserStatisticsVO statistics = adminUserService.getUserStatistics();
        return Response.success(statistics);
    }

    @PostMapping("/list")
    @Operation(summary = "分页查询用户列表")
    public Response<PageResult> list(@RequestBody AdminUserParam param) {
        PageResult result = adminUserService.listUsers(param);
        return Response.success(result);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情(含角色)")
    public Response<AdminUserVO> getById(@PathVariable Long userId) {
        AdminUserVO vo = adminUserService.getUserById(userId);
        return Response.success(vo);
    }

    @PostMapping("/create")
    @Operation(summary = "创建用户")
    public Response<Boolean> create(@Valid @RequestBody AdminUserCreateDTO dto) {
        boolean result = adminUserService.createUser(dto);
        return Response.success(result);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户")
    public Response<Boolean> update(@Valid @RequestBody AdminUserUpdateDTO dto) {
        boolean result = adminUserService.updateUser(dto);
        return Response.success(result);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户(逻辑删除)")
    public Response<Boolean> delete(@PathVariable Long userId) {
        boolean result = adminUserService.deleteUser(userId);
        return Response.success(result);
    }

    @PutMapping("/status/{userId}")
    @Operation(summary = "切换用户状态(禁用/启用)")
    public Response<Boolean> toggleStatus(@PathVariable Long userId) {
        boolean result = adminUserService.toggleStatus(userId);
        return Response.success(result);
    }

    @PostMapping("/assignRole")
    @Operation(summary = "为用户分配角色")
    public Response<Boolean> assignRole(@Valid @RequestBody AssignRoleDTO dto) {
        boolean result = adminUserService.assignRole(dto);
        return Response.success(result);
    }

    @PutMapping("/resetPassword")
    @Operation(summary = "重置用户密码")
    public Response<Boolean> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {
        boolean result = adminUserService.resetPassword(userId, newPassword);
        return Response.success(result);
    }
}
