package com.titancore.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(name = "PermissionVO", description = "权限详情VO")
public class PermissionVO implements Serializable {

    @Schema(description = "权限ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @Schema(description = "父ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "类型(1：目录 2：菜单 3：按钮)")
    private Integer type;

    @Schema(description = "菜单路由")
    private String menuUrl;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "权限标识")
    private String permissionKey;

    @Schema(description = "状态(0：启用；1：禁用)")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
