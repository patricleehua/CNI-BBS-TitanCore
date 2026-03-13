package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 检查角色是否被用户关联
     */
    int countUserByRoleId(@Param("roleId") Long roleId);
}
