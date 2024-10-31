package com.titancore.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.titancore.domain.dto.RegisterUserDTO;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.domain.vo.UserRegisterVo;
import com.titancore.domain.vo.UserVo;
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
    public Response<?> recommendedUser(@PathVariable(required = false) String userId){
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
}
