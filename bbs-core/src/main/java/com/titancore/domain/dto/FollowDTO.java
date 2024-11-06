package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "FollowDTO", description = "关注数据传输对象")
@NoArgsConstructor
public class FollowDTO extends BaseDTO{
    @Schema(description = "当前用户Id")
    @NonNull
    private String userId;
    @Schema(description = "关注者/被关注者Id")
    @NonNull
    private String followerId;
    @Schema(description = "分组Id")
    private String groupId;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "是否同意关注")
    private boolean isAcceptRequested;
    @Schema(description = "是否单向拉黑")
    private boolean isBlocked;

}
