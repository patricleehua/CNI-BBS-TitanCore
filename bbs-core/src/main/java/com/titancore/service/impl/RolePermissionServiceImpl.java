package com.titancore.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.RolePermission;
import com.titancore.domain.mapper.RolePermissionMapper;
import com.titancore.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 根据角色ID查权限
     * @param roleId
     * @return
     */
    public List<String> queryRoleCodeByRoleId(long roleId) {
        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id",roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(queryWrapper);
        List<String> roleCodes = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissions) {
            roleCodes.add(rolePermission.getPermissionCode());
        }
        return roleCodes;
    }
}
