package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "校验用户验证码DTO", description = "校验用户验证码数据传输对象")
public class ResetPasswordUserDTO extends BaseDTO {

    @Schema(description = "用户手机号", example = "13700000000")
    private String phoneNumber;

    @Schema(description = "用户邮箱", example = "test@gmail.com")
    private String emailNumber;

    @Schema(description = "临时通行码")
    @NonNull
    private String temporaryCode;

    @Schema(description = "新密码")
    @NonNull
    private String newPassword;

}

