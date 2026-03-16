package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "AdminUserParam", description = "管理员用户分页查询参数")
public class AdminUserParam extends PageParams {

    @Schema(description = "对外公开的用户名")
    private String publicUsername;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phoneNumber;

    @Schema(description = "帐号状态（0正常 1停用）")
    private String status;

    @Schema(description = "用户类型（00系统用户 01注册用户 02第三方平台注册用户）")
    private String userType;
}
