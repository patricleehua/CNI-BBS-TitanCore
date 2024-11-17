package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatGroupInviteMemberDTO", description = "群组成员邀请数据传输对象")
@NoArgsConstructor
public class ChatGroupInviteMemberDTO extends BaseDTO{

    @Schema(description = "群主Id")
    @NonNull
    private String ownerUserId;

    @Schema(description = "群号")
    @NonNull
    private String groupId;

    @Schema(description = "成员Id列表")
    private List<String> userIds;
}
