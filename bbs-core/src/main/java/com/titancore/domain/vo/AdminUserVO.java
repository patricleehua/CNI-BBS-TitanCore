package com.titancore.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(name = "AdminUserVO", description = "管理员用户详情VO")
public class AdminUserVO implements Serializable {

    @Schema(description = "用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @Schema(description = "对外公开的用户名")
    private String publicUsername;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户类型")
    private String userType;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phoneNumber;

    @Schema(description = "用户性别")
    private String sex;

    @Schema(description = "头像路径")
    private String avatar;

    @Schema(description = "帐号状态")
    private String status;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime loginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "角色列表")
    private List<RoleVO> roles;
}
