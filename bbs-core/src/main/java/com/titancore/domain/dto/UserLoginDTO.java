package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * callSuper = true 指定了在生成 equals() 和 hashCode() 方法时，应该调用超类（如 BaseDTO）的 equals() 和 hashCode() 方法。这会将超类的字段纳入比较逻辑中。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "UserLoginDTO", description = "用户登录数据传输对象")
public class UserLoginDTO  extends BaseDTO {

    @Schema(description = "用户名称/登入名称", example = "test", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "用户密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "记住我选项，值为0或1(0为记住），决定是否保持用户登录状态", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    private String rememberMe;

    @Schema(description = "登录类型 值为'v_code'或'password或t_code'", example = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    private String loginType;

    @Schema(description = "用户手机号", example = "13700000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phoneNumber;

    @Schema(description = "用户邮箱", example = "test@gmail.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String emailNumber;

    @Schema(description = "验证码类型 值为'phone'或'email'", example = "phone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String captchaType;

    @Schema(description = "验证码", example = "123456", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String captchaCode;
}

