package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatGroupKickDTO", description = "群组成员踢出数据传输对象")
@NoArgsConstructor
public class ChatGroupKickDTO extends BaseDTO{

    @Schema(description = "群主/管理员Id")
    @NonNull
    private String managerId;
    @Schema(description = "成员Id")
    @NonNull
    private String userId;
    @Schema(description = "群号")
    @NonNull
    private String groupId;
}
