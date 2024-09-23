package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送验证码 的类型
 */
@Data
@Schema(name = "验证码DTO", description = "验证码数据传输对象")
public class CaptchaCodeDTO implements Serializable {

    @Schema(description = "用户手机号", example = "13711111111", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phoneNumber;

    @Schema(description = "用户邮箱", example = "user@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String emailNumber;

    @Schema(description = "验证码类型，值为'phone'或'email'", example = "phone", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaType; // phone/email

    @Schema(description = "验证码内容", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaCode;

    @Schema(description = "是否注册，值为0表示是，1表示否", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isRegister; // 0/1
}
