package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(name = "AdminUserCreateDTO", description = "管理员创建用户DTO")
public class AdminUserCreateDTO {

    @NotBlank(message = "登录账号不能为空")
    @Schema(description = "登录账号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginName;

    @NotBlank(message = "用户昵称不能为空")
    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phoneNumber;

    @Schema(description = "用户性别（0男 1女 -1未知）")
    private String sex;

    @Schema(description = "头像路径")
    private String avatar;

    @Schema(description = "用户类型（00系统用户 01注册用户 02第三方平台注册用户）")
    private String userType;

    @Schema(description = "帐号状态（0正常 1停用）")
    private String status;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "备注")
    private String remark;
}
