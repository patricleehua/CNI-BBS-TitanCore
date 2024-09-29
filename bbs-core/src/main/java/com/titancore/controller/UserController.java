package com.titancore.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.framework.biz.operationlog.aspect.ApiOperationLog;
import com.titancore.framework.common.response.Response;
import com.titancore.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        log.info("login:"+userLoginDTO);
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
        return Response.success(true);
    }
}
