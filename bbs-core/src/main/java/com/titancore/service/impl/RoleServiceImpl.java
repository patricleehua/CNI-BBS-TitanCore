package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.AssignPermissionDTO;
import com.titancore.domain.dto.RoleDTO;
import com.titancore.domain.entity.Permission;
import com.titancore.domain.entity.Role;
import com.titancore.domain.entity.RolePermission;
import com.titancore.domain.mapper.PermissionMapper;
import com.titancore.domain.mapper.RoleMapper;
import com.titancore.domain.mapper.RolePermissionMapper;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.RoleParam;
import com.titancore.domain.vo.PermissionVO;
import com.titancore.domain.vo.RoleDetailVO;
import com.titancore.domain.vo.RoleVO;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    private static final List<Long> PRESET_ROLE_IDS = List.of(1L, 2L, 3L, 4L, 5L, 6L);

    @Override
    public PageResult listRoles(RoleParam param) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getIsDeleted, false);

        if (StringUtils.isNotBlank(param.getRoleName())) {
            wrapper.like(Role::getRoleName, param.getRoleName());
        }
        if (StringUtils.isNotBlank(param.getRoleKey())) {
            wrapper.like(Role::getRoleKey, param.getRoleKey());
        }
        if (param.getStatus() != null) {
            wrapper.eq(Role::getStatus, param.getStatus());
        }
        wrapper.orderByAsc(Role::getSort);

        Page<Role> page = new Page<>(param.getPageNo(), param.getPageSize());
        Page<Role> result = page(page, wrapper);

        List<RoleVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());

        PageResult pageResult = new PageResult();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    public List<RoleVO> listAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getIsDeleted, false)
               .eq(Role::getStatus, 1)
               .orderByAsc(Role::getSort);
        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public RoleDetailVO getRoleById(Long roleId) {
        Role role = getById(roleId);
        if (role == null || role.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        RoleDetailVO detailVO = new RoleDetailVO();
        BeanUtils.copyProperties(role, detailVO);

        // 查询角色关联的权限
        List<Permission> permissions = rolePermissionMapper.queryPermissionByRoleId(roleId);
        List<PermissionVO> permissionVOs = permissions.stream().map(p -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).collect(Collectors.toList());
        detailVO.setPermissions(permissionVOs);

        return detailVO;
    }

    @Override
    @Transactional
    public boolean createRole(RoleDTO dto) {
        // 检查roleKey是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, dto.getRoleKey())
               .eq(Role::getIsDeleted, false);
        if (count(wrapper) > 0) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        role.setIsDeleted(false);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        return save(role);
    }

    @Override
    @Transactional
    public boolean updateRole(RoleDTO dto) {
        if (dto.getId() == null) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        Role existing = getById(dto.getId());
        if (existing == null || existing.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 如果roleKey被修改，检查是否重复
        if (!existing.getRoleKey().equals(dto.getRoleKey())) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getRoleKey, dto.getRoleKey())
                   .eq(Role::getIsDeleted, false)
                   .ne(Role::getId, dto.getId());
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
    public boolean deleteRole(Long roleId) {
        // 检查是否是预置角色
        if (PRESET_ROLE_IDS.contains(roleId)) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        Role role = getById(roleId);
        if (role == null || role.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 检查是否有用户关联
        int userCount = roleMapper.countUserByRoleId(roleId);
        if (userCount > 0) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        role.setIsDeleted(true);
        role.setUpdateTime(LocalDateTime.now());
        return updateById(role);
    }

    @Override
    @Transactional
    public boolean toggleStatus(Long roleId) {
        Role role = getById(roleId);
        if (role == null || role.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        role.setStatus(role.getStatus() == 1 ? 0 : 1);
        role.setUpdateTime(LocalDateTime.now());
        return updateById(role);
    }

    @Override
    @Transactional
    public boolean assignPermission(AssignPermissionDTO dto) {
        Role role = getById(dto.getRoleId());
        if (role == null || role.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 删除原有权限关系
        LambdaQueryWrapper<RolePermission> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(RolePermission::getRoleId, dto.getRoleId());
        rolePermissionMapper.delete(deleteWrapper);

        // 批量插入新权限关系
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            for (Long permissionId : dto.getPermissionIds()) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(dto.getRoleId());
                rp.setPermissionId(permissionId);
                rp.setCreateTime(LocalDateTime.now());
                rp.setUpdateTime(LocalDateTime.now());
                rp.setIsDeleted(false);
                rolePermissionMapper.insert(rp);
            }
        }
        return true;
    }

    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
