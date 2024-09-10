package com.titancore.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 发送验证码 的类型
 */
@Data
public class CaptchaCodeDTO implements Serializable {
    private String phoneNumber;
    private String emailNumber;
    private String captchaType;//phone/email
    private String captchaCode;
    private Integer isRegister;//0/1
}
