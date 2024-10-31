package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "积分记录DTO", description = "积分记录数据传输对象")
@NoArgsConstructor
public class PointsRecordDTO extends BaseDTO{

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "积分规则ID")
    private String ruleId;

    @Schema(description = "积分数")
    private Integer points;

    @Schema(description = "积分状态")
    private String status;

    @Schema(description = "过期时间")
    private LocalDateTime expirationTime;

    @Schema(description = "已使用积分数")
    private Integer usedPoints;

}
