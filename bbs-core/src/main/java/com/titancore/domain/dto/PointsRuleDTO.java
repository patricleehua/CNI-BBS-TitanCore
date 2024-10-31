package com.titancore.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "积分规则DTO", description = "积分规则数据传输对象")
@NoArgsConstructor
public class PointsRuleDTO extends BaseDTO{

    @Schema(description = "积分规则Id")
    private String id;

    @Schema(description = "规则描述")
    @NonNull
    private String description;

    @Schema(description = "积分规则唯一标识")
    @NonNull
    private String uniqueKey;

    @Schema(description = "积分数")
    @NonNull
    private Integer points;

    @Schema(description = "行为类型")
    @NonNull
    private String behaviorType;

    @Schema(description = "是否启用")
    private Integer isActive;

    @Schema(description = "有效期（天）")
    @NonNull
    private Integer validityPeriod;

}
