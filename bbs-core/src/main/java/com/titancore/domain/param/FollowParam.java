package com.titancore.domain.param;

import com.titancore.framework.common.request.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Schema(name = "关注参数", description = "关注参数对象")
public class FollowParam extends PageParams {

    @Schema(description = "用户Id")
    @NonNull
    private String userId;
    @Schema(description = "是否检查看关注者/否为查看自己关注的")
    private boolean isCheckFollower;
    @Schema(description = "关注者信息")
    private String friendsInfo;

}
