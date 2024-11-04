package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.*;
import com.titancore.domain.entity.User;
import com.titancore.domain.vo.*;

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

    /**
     * 用户上线
     * @param token
     * @return
     */
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
    UserRegisterVo register(RegisterUserDTO registerUserDTO);

    /**
     * 用户下线
     * @param userId
     */
    void offline(String userId);

    /**
     * 检查用户账号是否存在
     * @param account
     * @return
     */
    boolean checkUserIfExist(String account);

    /**
     * 校验用户验证码
     * @param verificationCodeForUserDTO
     * @return
     */
    UserVerificationCodeVo checkUserVerificationCode(VerificationCodeForUserDTO verificationCodeForUserDTO);

    /**
     * 重置密码
     * @param resetPasswordUserDTO
     * @return
     */
    UserResetPasswordVo resetPassword(ResetPasswordUserDTO resetPasswordUserDTO);

    /**
     * 给key生成通行码
     * @param key
     * @return
     */
    int getTemporaryPassCode(String key);

    /**
     * 校验key的通行码
     * @param key
     * @param verifyCode
     * @return
     */
    boolean verifyTemporaryPassCode(String key,String verifyCode);

    /**
     * 为第三方平台用户建立本地数据用户关联
     * @param bindSocialUserDTO
     * @return
     */
    DMLVo socialUserBindLocalUser(BindSocialUserDTO bindSocialUserDTO);
}

