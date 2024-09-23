package com.titancore.controller;

import com.titancore.domain.dto.CaptchaCodeDTO;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.framework.common.response.Response;
import com.titancore.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/common")
@Tag(name = "公共模块")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * 发送验证码
     * 更具类型进行判断发送何种类型的验证码
     *
     * @param captchaCodeDTO
     * @return
     */
    @PostMapping("/open/sendCode")
    @Operation(summary = "发送验证码",description = "可发送邮箱/手机验证码")
    public Response<?> sendCode(@RequestBody CaptchaCodeDTO captchaCodeDTO){
        log.info("发送验证码:{}",captchaCodeDTO);
        return commonService.sendCaptchaCode(captchaCodeDTO);
    }
}
