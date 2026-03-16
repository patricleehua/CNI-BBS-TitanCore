package com.titancore.service;

import com.titancore.domain.dto.AdminUserCreateDTO;
import com.titancore.domain.dto.AdminUserUpdateDTO;
import com.titancore.domain.dto.AssignRoleDTO;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.AdminUserParam;
import com.titancore.domain.vo.AdminUserStatisticsVO;
import com.titancore.domain.vo.AdminUserVO;

public interface AdminUserService {

    /**
     * 分页查询用户列表
     */
    PageResult listUsers(AdminUserParam param);

    /**
     * 获取用户详情(含角色)
     */
    AdminUserVO getUserById(Long userId);

    /**
     * 创建用户
     */
    boolean createUser(AdminUserCreateDTO dto);

    /**
     * 更新用户
     */
    boolean updateUser(AdminUserUpdateDTO dto);

    /**
     * 删除用户(逻辑删除)
     */
    boolean deleteUser(Long userId);

    /**
     * 切换用户状态
     */
    boolean toggleStatus(Long userId);

    /**
     * 为用户分配角色
     */
    boolean assignRole(AssignRoleDTO dto);

    /**
     * 重置用户密码
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 获取用户统计数据
     */
    AdminUserStatisticsVO getUserStatistics();
}
