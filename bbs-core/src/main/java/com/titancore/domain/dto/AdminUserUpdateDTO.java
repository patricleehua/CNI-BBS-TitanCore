package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Schema(name = "AdminUserUpdateDTO", description = "管理员更新用户DTO")
public class AdminUserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "用户昵称")
    private String userName;

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

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "备注")
    private String remark;
}
