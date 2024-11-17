package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChatGroupUpdateDTO", description = "群组数据更新传输对象")
@NoArgsConstructor
public class ChatGroupUpdateDTO extends BaseDTO{

    @Schema(description = "群号")
    @NonNull
    private String groupId;

    @Schema(description = "群名")
    private String groupName;

    @Schema(description = "群头像")
    private String portrait;

    @Schema(description = "是否公开")
    private Integer isOpen;

    @Schema(description = "群主Id")
    @NonNull
    private String ownerUserId;

    @Schema(description = "新群主Id")
    private String newOwnerUserId;

}
