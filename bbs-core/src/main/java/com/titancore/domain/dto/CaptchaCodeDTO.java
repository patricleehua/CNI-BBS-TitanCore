package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


/**
 * 发送验证码 的类型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "CaptchaCodeDTO", description = "验证码数据传输对象")
public class CaptchaCodeDTO extends BaseDTO {

    @Schema(description = "用户手机号", example = "13700000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phoneNumber;

    @Schema(description = "用户邮箱", example = "test@gmail.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String emailNumber;

    @Schema(description = "验证码类型，值为'phone'或'email'", example = "phone", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    private String captchaType;

    @Schema(description = "用户收到的验证码内容（由系统自动生成）", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaCode;

    @Schema(description = "是否注册，值为0表示是，1表示否", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NonNull
    private Integer isRegister;
}
