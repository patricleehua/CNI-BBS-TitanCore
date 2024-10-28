package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息表(User)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-22 18:17:51
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


    List<User> selectListByIds(@Param("ids") List<Long> userIds);
}

