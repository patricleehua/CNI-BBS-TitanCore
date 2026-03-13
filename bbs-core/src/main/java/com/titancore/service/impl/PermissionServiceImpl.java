package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.PermissionDTO;
import com.titancore.domain.entity.Permission;
import com.titancore.domain.mapper.PermissionMapper;
import com.titancore.domain.mapper.RolePermissionMapper;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PermissionParam;
import com.titancore.domain.vo.PermissionTreeVO;
import com.titancore.domain.vo.PermissionVO;
import com.titancore.framework.common.exception.BizException;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.service.PermissionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public PageResult listPermissions(PermissionParam param) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getIsDeleted, false);

        if (StringUtils.isNotBlank(param.getName())) {
            wrapper.like(Permission::getName, param.getName());
        }
        if (StringUtils.isNotBlank(param.getPermissionKey())) {
            wrapper.like(Permission::getPermissionKey, param.getPermissionKey());
        }
        if (param.getType() != null) {
            wrapper.eq(Permission::getType, param.getType());
        }
        if (param.getStatus() != null) {
            wrapper.eq(Permission::getStatus, param.getStatus());
        }
        wrapper.orderByAsc(Permission::getSort);

        Page<Permission> page = new Page<>(param.getPageNo(), param.getPageSize());
        Page<Permission> result = page(page, wrapper);

        List<PermissionVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());

        PageResult pageResult = new PageResult();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    public List<PermissionTreeVO> getPermissionTree() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getIsDeleted, false)
               .eq(Permission::getStatus, 1)
               .orderByAsc(Permission::getSort);
        List<Permission> allPermissions = list(wrapper);
        return buildTree(allPermissions, 0L);
    }

    @Override
    public PermissionVO getPermissionById(Long permissionId) {
        Permission permission = getById(permissionId);
        if (permission == null || permission.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        return toVO(permission);
    }

    @Override
    @Transactional
    public boolean createPermission(PermissionDTO dto) {
        // 检查permissionKey是否已存在
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionKey, dto.getPermissionKey())
               .eq(Permission::getIsDeleted, false);
        if (count(wrapper) > 0) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(dto, permission);
        permission.setIsDeleted(false);
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        return save(permission);
    }

    @Override
    @Transactional
    public boolean updatePermission(PermissionDTO dto) {
        if (dto.getId() == null) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        Permission existing = getById(dto.getId());
        if (existing == null || existing.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 如果permissionKey被修改，检查是否重复
        if (!existing.getPermissionKey().equals(dto.getPermissionKey())) {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Permission::getPermissionKey, dto.getPermissionKey())
                   .eq(Permission::getIsDeleted, false)
                   .ne(Permission::getId, dto.getId());
            if (count(wrapper) > 0) {
                throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
            }
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }

    @Override
    @Transactional
    public boolean deletePermission(Long permissionId) {
        Permission permission = getById(permissionId);
        if (permission == null || permission.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 检查是否有角色关联
        int roleCount = permissionMapper.countRoleByPermissionId(permissionId);
        if (roleCount > 0) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 检查是否有子权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, permissionId)
               .eq(Permission::getIsDeleted, false);
        if (count(wrapper) > 0) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        permission.setIsDeleted(true);
        permission.setUpdateTime(LocalDateTime.now());
        return updateById(permission);
    }

    private PermissionVO toVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }

    private List<PermissionTreeVO> buildTree(List<Permission> permissions, Long parentId) {
        List<PermissionTreeVO> tree = new ArrayList<>();
        for (Permission permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                PermissionTreeVO node = new PermissionTreeVO();
                BeanUtils.copyProperties(permission, node);
                node.setChildren(buildTree(permissions, permission.getId()));
                tree.add(node);
            }
        }
        return tree;
    }
}
