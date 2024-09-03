package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.RolePermission;

import java.util.List;


public interface RolePermissionService  extends IService<RolePermission> {
    /**
     * 根据角色ID查权限
     * @param roleId
     * @return
     */
    List<String> queryRoleCodeByRoleId(long roleId);

}
