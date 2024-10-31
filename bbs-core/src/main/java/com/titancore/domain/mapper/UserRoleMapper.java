package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.titancore.domain.entity.Role;
import com.titancore.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户Id查询对应的角色列表
     * @param userId
     * @return
     */
    List<Role> queryRoleByUserId(@Param("userId") Long userId);

    /**
     * 构建用户权限关联关系
     * @param id
     * @param userId
     * @param roleId
     * @return
     */
    int buildUserRoleRelByUserId(@Param("id") Long id,@Param("userId") Long userId,@Param("roleId") Long roleId);
}

