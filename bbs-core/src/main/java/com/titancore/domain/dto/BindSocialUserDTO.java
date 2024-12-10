package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "BindSocialUserDTO", description = "绑定第三方用户数据传输对象")
public class BindSocialUserDTO extends BaseDTO {

    @Schema(description = "用户手机号", example = "13700000000")
    private String phoneNumber;

    @Schema(description = "用户邮箱", example = "test@gmail.com")
    private String emailNumber;

    @Schema(description = "用户邮箱/手机号临时通行码")
    @NonNull
    private String accountTemporaryCode;

    @Schema(description = "password", example = "123456")
    private String password;

    @Schema(description = "第三方用户临时通行码")
    @NonNull
    private String socialTemporaryCode;

    @Schema(description = "第三方用户Id")
    @NonNull
    private String socialUserId;

    @Schema(description = "是否已有本地账号（否为创建新的账号）")
    @NonNull
    private Boolean hasLocalUser;

}

