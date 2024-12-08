package com.titancore.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.titancore.domain.dto.*;
import com.titancore.domain.vo.*;
import com.titancore.framework.biz.operationlog.aspect.ApiOperationLog;
import com.titancore.framework.common.response.Response;
import com.titancore.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户模块")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/open/login")
    @ApiOperationLog
    @Operation(summary = "用户登入")
    public Response<?> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("login:{}", userLoginDTO);
        UserLoginVo userLoginVo = userService.login(userLoginDTO);
        return Response.success(userLoginVo);
    }

    @GetMapping("/open/logout/{userId}")
    @ApiOperationLog
    @Operation(summary = "用户退出")
    public Response<?> logOut(@PathVariable String userId){
        log.info("用户退出:{}",userId);
        StpUtil.logout(userId);
        return Response.success();
    }

    @GetMapping("/checkUserStoresLive")
    @Operation(summary = "检查用户本地存储信息状态")
    public Response<?> checkUserStoresLive(){
        StpUtil.checkLogin();
        return Response.success(StpUtil.getLoginId().toString());
    }

    @GetMapping({"/open/recommendedUser", "/recommendedUser/{userId}"})
    @ApiOperationLog
    @Operation(summary = "推荐关注用户（待改善推荐算法）")
    public Response<?> recommendedUser(@PathVariable(name="userId",required = false) String userId){
        List<UserVo> userVos = new ArrayList<>();
        if(userId == null){
            userVos =  userService.recommendedUserAll();
        }else{
//            userVos =  userService.recommendedUserByUserId(userId);
        }

        return Response.success(userVos);
    }
    @PostMapping("/open/register")
    @Operation(summary = "用户注册")
    public Response<?> register(@RequestBody RegisterUserDTO registerUserDTO){
        UserRegisterVo userRegisterVo = userService.register(registerUserDTO);
        return Response.success(userRegisterVo);
    }
    @GetMapping("/open/checkUserIfExist/{account}")
    @Operation(summary = "检查用户是否存在于系统")
    public Response<?> checkUserIfExist(@PathVariable String account){
        boolean result = userService.checkUserIfExist(account);
        return Response.success(result);
    }
    @PostMapping("/open/checkUserVerificationCode")
    @Operation(summary = "检查用户验证码并生成临时通行凭证")
    public Response<?> checkUserVerificationCode(@RequestBody VerificationCodeForUserDTO verificationCodeForUserDTO){
        UserVerificationCodeVo userVerificationCodeVo = userService.checkUserVerificationCode(verificationCodeForUserDTO);
        return Response.success(userVerificationCodeVo);
    }
    @PostMapping("/open/resetPassword")
    @Operation(summary = "用户重置密码")
    public Response<?> resetPassword(@RequestBody ResetPasswordUserDTO resetPasswordUserDTO){
        UserResetPasswordVo userResetPasswordVo = userService.resetPassword(resetPasswordUserDTO);
        return Response.success(userResetPasswordVo);
    }
    @PostMapping("/open/socialUserBindLocalUser")
    @Operation(summary = "第三方用户绑定本地用户")
    public Response<?> socialUserBindLocalUser(@RequestBody BindSocialUserDTO bindSocialUserDTO){
        boolean isVerify = userService.verifyTemporaryPassCode(bindSocialUserDTO.getSocialUserId(), bindSocialUserDTO.getTemporaryCode());
        if(isVerify){
            DMLVo dmlVo = userService.socialUserBindLocalUser(bindSocialUserDTO);
            return Response.success(dmlVo);
        }
        return Response.success();
    }

}
