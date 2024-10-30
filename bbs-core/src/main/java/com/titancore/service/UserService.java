package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.RegisterUserDTO;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.entity.User;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.domain.vo.UserVo;

import java.util.List;

/**
 * 用户信息表(User)表服务接口
 *
 * @author makejava
 * @since 2023-11-22 18:16:47
 */
public interface UserService  extends IService<User> {

    /**
     * 登入
     * @param userLoginDto
     * @return
     */
    UserLoginVo login(UserLoginDTO userLoginDto);


    String onlineByToken(String token);

    /**
     * 推荐用户
     * @return
     */
    List<UserVo> recommendedUserAll();

    /**
     * 用户注册
     * @param registerUserDTO
     * @return
     */
    UserLoginVo register(RegisterUserDTO registerUserDTO);
}

