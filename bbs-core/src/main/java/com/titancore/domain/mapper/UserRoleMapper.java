package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.titancore.domain.entity.User;
import com.titancore.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

   List<User> selectUserByRole (Long roleId);
}

