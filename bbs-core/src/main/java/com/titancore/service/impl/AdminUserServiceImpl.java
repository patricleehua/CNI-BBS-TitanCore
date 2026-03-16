package com.titancore.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.titancore.domain.dto.AdminUserCreateDTO;
import com.titancore.domain.dto.AdminUserUpdateDTO;
import com.titancore.domain.dto.AssignRoleDTO;
import com.titancore.domain.entity.Role;
import com.titancore.domain.entity.User;
import com.titancore.domain.entity.UserRole;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.mapper.UserRoleMapper;
import com.titancore.domain.param.AdminUserParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.AdminUserVO;
import com.titancore.domain.vo.RoleVO;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.properties.Md5Salt;
import com.titancore.service.AdminUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final Md5Salt md5Salt;
    private final SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();

    @Override
    public PageResult listUsers(AdminUserParam param) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDelFlag, "0");

        if (StringUtils.isNotBlank(param.getLoginName())) {
            wrapper.like(User::getLoginName, param.getLoginName());
        }
        if (StringUtils.isNotBlank(param.getUserName())) {
            wrapper.like(User::getUserName, param.getUserName());
        }
        if (StringUtils.isNotBlank(param.getEmail())) {
            wrapper.like(User::getEmail, param.getEmail());
        }
        if (StringUtils.isNotBlank(param.getPhoneNumber())) {
            wrapper.like(User::getPhoneNumber, param.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(param.getStatus())) {
            wrapper.eq(User::getStatus, param.getStatus());
        }
        if (StringUtils.isNotBlank(param.getUserType())) {
            wrapper.eq(User::getUserType, param.getUserType());
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(param.getPageNo(), param.getPageSize());
        Page<User> result = userMapper.selectPage(page, wrapper);

        List<AdminUserVO> voList = result.getRecords().stream().map(user -> {
            AdminUserVO vo = toVO(user);
            // 查询用户角色
            List<Role> roles = userRoleMapper.queryRoleByUserId(user.getUserId());
            List<RoleVO> roleVOs = roles.stream().map(role -> {
                RoleVO roleVO = new RoleVO();
                BeanUtils.copyProperties(role, roleVO);
                return roleVO;
            }).collect(Collectors.toList());
            vo.setRoles(roleVOs);
            return vo;
        }).collect(Collectors.toList());

        PageResult pageResult = new PageResult();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    public AdminUserVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        AdminUserVO vo = toVO(user);
        // 查询用户角色
        List<Role> roles = userRoleMapper.queryRoleByUserId(userId);
        List<RoleVO> roleVOs = roles.stream().map(role -> {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(role, roleVO);
            return roleVO;
        }).collect(Collectors.toList());
        vo.setRoles(roleVOs);
        return vo;
    }

    @Override
    @Transactional
    public boolean createUser(AdminUserCreateDTO dto) {
        // 检查账号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getLoginName, dto.getLoginName())
               .eq(User::getDelFlag, "0");
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_LOGINNAME_IS_ALLERY_EXIST);
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        // 密码加密
        String password = dto.getPassword() + md5Salt.getSalt();
        String md5password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(md5password);
        user.setDelFlag("0");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isBlank(user.getStatus())) {
            user.setStatus("1");
        }
        if (StringUtils.isBlank(user.getUserType())) {
            user.setUserType("01");
        }

        int result = userMapper.insert(user);
        if (result > 0) {
            // 默认分配registered_user角色(id=5)
            UserRole userRole = UserRole.builder()
                    .id(snowflakeGenerator.next())
                    .userId(user.getUserId())
                    .roleId(5L)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .isDeleted(false)
                    .build();
            userRoleMapper.insert(userRole);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateUser(AdminUserUpdateDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        BeanUtils.copyProperties(dto, user);
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        user.setDelFlag("2");
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean toggleStatus(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        String newStatus = "1".equals(user.getStatus()) ? "0" : "1";
        user.setStatus(newStatus);
        user.setUpdateTime(LocalDateTime.now());

        // 如果禁用用户，踢出登录态
        if ("0".equals(newStatus)) {
            StpUtil.kickout(userId);
        }

        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean assignRole(AssignRoleDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 删除原有角色关系
        userRoleMapper.deleteUserRoleByUserId(dto.getUserId());

        // 批量插入新角色关系
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            for (Long roleId : dto.getRoleIds()) {
                UserRole userRole = UserRole.builder()
                        .id(snowflakeGenerator.next())
                        .userId(dto.getUserId())
                        .roleId(roleId)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .isDeleted(false)
                        .build();
                userRoles.add(userRole);
            }
            userRoleMapper.batchInsertUserRole(userRoles);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        String password = newPassword + md5Salt.getSalt();
        String md5password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUserId, userId)
                     .set(User::getPassword, md5password)
                     .set(User::getPwdUpdateTime, LocalDateTime.now())
                     .set(User::getUpdateTime, LocalDateTime.now());

        // 踢出登录态
        StpUtil.kickout(userId);

        return userMapper.update(null, updateWrapper) > 0;
    }

    private AdminUserVO toVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
