package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.Permission;
import com.titancore.domain.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RolePermissionMapper  extends BaseMapper<RolePermission> {

    /**
     * 根据角色Id查询所有的权限
     * @param roleId
     * @return
     */
    List<Permission> queryPermissionByRoleId(Long roleId);

}

