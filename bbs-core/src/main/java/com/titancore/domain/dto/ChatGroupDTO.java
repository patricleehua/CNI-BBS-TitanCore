package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "群组DTO", description = "群组数据传输对象")
public class ChatGroupDTO extends BaseDTO{
    @Schema(description = "群号")
    private String groupId;
    @Schema(description = "群名")
    private String groupName;
    @Schema(description = "公告")
    private String notice;
    @Schema(description = "是否公开")
    private Integer isOpen;
    @Schema(description = "创建者Id")
    private String userId;
    @Schema(description = "群主Id")
    private String ownerUserId;

    @Schema(description = "成员Id列表")
    private List<String> userIds;


}
