package com.titancore.controller;

import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.entity.User;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.framework.common.response.Response;
import com.titancore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public Response<?> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("login:"+userLoginDTO);
        UserLoginVo userLoginVo = userService.login(userLoginDTO);
        return Response.success(userLoginVo);
    }
}
