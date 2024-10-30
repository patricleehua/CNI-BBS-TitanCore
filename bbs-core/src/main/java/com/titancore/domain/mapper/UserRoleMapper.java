package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.titancore.domain.entity.Role;
import com.titancore.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户Id查询对应的角色列表
     * @param user_Id
     * @return
     */
    List<Role> queryRoleByUserId(Long user_Id);

    /**
     * 构建用户权限关联关系
     * @param id
     * @param user_Id
     * @param role_Id
     * @return
     */
    int buildUserRoleRelByUserId(Long id,Long user_Id, Long role_Id);
}

