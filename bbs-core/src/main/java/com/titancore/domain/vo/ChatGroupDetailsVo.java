package com.titancore.domain.vo;


import com.titancore.domain.entity.ChatGroupNotice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Schema(description = "聊天群详细信息响应对象")
public class ChatGroupDetailsVo {

    @Schema(description = "群号")
    private String groupId;
    @Schema(description = "群名")
    private String groupName;
    @Schema(description = "创建者")
    private String createUserName;
    @Schema(description = "群主")
    private String ownerUserName;
    @Schema(description = "群头像")
    private String portrait;
    @Schema(description = "是否公开（0/1）")
    private String isOpen;
    @Schema(description = "群通知")
    private ChatGroupNotice notice;
    @Schema(description = "群成员总数")
    private String memberNum;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
