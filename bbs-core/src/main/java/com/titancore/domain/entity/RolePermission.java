package com.titancore.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (RolePermission)实体类
 *
 * @author makejava
 * @since 2024-03-14 19:37:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission implements Serializable {
/**
     * id
     */
    private Long id;
/**
     * 角色ID
     */
    private Integer roleId;
/**
     * 权限菜单
     */
    private String permissionCode;
/**
     * 创建时间
     */
    private Long createTime;



}

