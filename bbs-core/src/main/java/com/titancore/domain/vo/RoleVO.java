package com.titancore.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(name = "RoleVO", description = "角色详情VO")
public class RoleVO implements Serializable {

    @Schema(description = "角色ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色唯一标识")
    private String roleKey;

    @Schema(description = "状态(0：启用 1：禁用)")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
