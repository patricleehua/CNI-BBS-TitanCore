package com.titancore.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员用户统计VO
 */
@Data
@Schema(description = "用户统计数据")
public class AdminUserStatisticsVO {

    @Schema(description = "用户总数")
    private Long totalUsers;

    @Schema(description = "当前在线用户数")
    private Long onlineUsers;

    @Schema(description = "已封禁账户数")
    private Long bannedUsers;
}
