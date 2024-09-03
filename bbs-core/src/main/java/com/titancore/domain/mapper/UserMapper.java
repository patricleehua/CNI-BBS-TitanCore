package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.titancore.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户信息表(User)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-22 18:17:51
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user")
    User getall();

    /**
     * 按用户名查找具有角色的用户
     * @param username
     * @return
     */

    User findUserWithRolesByUserName(String username);
    IPage<User> serachUser(Page<User> page,
                         String KeyWords);
  int updateUser(User user);

    int deleteUser(Long userid);

}

