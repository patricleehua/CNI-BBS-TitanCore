package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "用户注册DTO", description = "用户注册数据传输对象")
public class RegisterUserDTO extends BaseDTO {

    @Schema(description = "登入名称", example = "123456aa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginName;

    @Schema(description = "用户名称", example = "test", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(description = "性别", example = "0/1/2", requiredMode = Schema.RequiredMode.REQUIRED)
    private int sex;

    @Schema(description = "用户密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "用户手机号", example = "13700000000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @Schema(description = "用户邮箱", example = "test@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emailNumber;

    @Schema(description = "个人简介", example = "你好世界！", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bio;

    @Schema(description = "邀请链接", example = "url.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String inviteUrl;

    @Schema(description = "是否为私有用户", example = "0/1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int isPrivate;

    @Schema(description = "备注（同意网站隐私）", example = "1为同意许可组成，2为不同意，3为未知）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;
}

