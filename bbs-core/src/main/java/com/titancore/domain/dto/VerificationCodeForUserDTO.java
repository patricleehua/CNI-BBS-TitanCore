package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "VerificationCodeForUserDTO", description = "校验用户验证码数据传输对象")
public class VerificationCodeForUserDTO extends BaseDTO {

    @Schema(description = "用户手机号", example = "13700000000")
    private String phoneNumber;

    @Schema(description = "用户邮箱", example = "test@gmail.com")
    private String emailNumber;

    @Schema(description = "验证码")
    @NonNull
    private String code;

}

