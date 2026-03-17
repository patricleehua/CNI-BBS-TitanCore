package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "UserUpdateDTO", description = "用户更新个人信息DTO")
public class UserUpdateDTO {

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户性别（0男 1女 -1未知）")
    private String sex;

    @Schema(description = "头像路径")
    private String avatar;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "用户类型（0私有账户 1公开账户）")
    private Integer isPrivate;
}
