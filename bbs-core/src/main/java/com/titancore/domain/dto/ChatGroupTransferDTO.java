package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatGroupTransferDTO", description = "群组转移数据传输对象")
@NoArgsConstructor
public class ChatGroupTransferDTO extends BaseDTO{

    @Schema(description = "群主Id")
    @NonNull
    private String ownerId;
    @Schema(description = "被转群成员Id")
    @NonNull
    private String userId;
    @Schema(description = "群号")
    @NonNull
    private String groupId;
}
