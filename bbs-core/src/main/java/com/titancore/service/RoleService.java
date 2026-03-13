package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.AssignPermissionDTO;
import com.titancore.domain.dto.RoleDTO;
import com.titancore.domain.entity.Role;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.RoleParam;
import com.titancore.domain.vo.RoleDetailVO;
import com.titancore.domain.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     */
    PageResult listRoles(RoleParam param);

    /**
     * 获取所有启用的角色(下拉选择用)
     */
    List<RoleVO> listAllRoles();

    /**
     * 获取角色详情(含权限)
     */
    RoleDetailVO getRoleById(Long roleId);

    /**
     * 创建角色
     */
    boolean createRole(RoleDTO dto);

    /**
     * 更新角色
     */
    boolean updateRole(RoleDTO dto);

    /**
     * 删除角色
     */
    boolean deleteRole(Long roleId);

    /**
     * 切换角色状态
     */
    boolean toggleStatus(Long roleId);

    /**
     * 为角色分配权限
     */
    boolean assignPermission(AssignPermissionDTO dto);
}
