package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.PermissionDTO;
import com.titancore.domain.entity.Permission;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PermissionParam;
import com.titancore.domain.vo.PermissionTreeVO;
import com.titancore.domain.vo.PermissionVO;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    /**
     * 分页查询权限列表
     */
    PageResult listPermissions(PermissionParam param);

    /**
     * 获取权限树形结构
     */
    List<PermissionTreeVO> getPermissionTree();

    /**
     * 获取权限详情
     */
    PermissionVO getPermissionById(Long permissionId);

    /**
     * 创建权限
     */
    boolean createPermission(PermissionDTO dto);

    /**
     * 更新权限
     */
    boolean updatePermission(PermissionDTO dto);

    /**
     * 删除权限
     */
    boolean deletePermission(Long permissionId);
}
