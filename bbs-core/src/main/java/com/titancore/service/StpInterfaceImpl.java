package com.titancore.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.util.SaFoxUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.titancore.domain.entity.Permission;
import com.titancore.domain.entity.Role;
import com.titancore.domain.entity.UserRole;
import com.titancore.domain.mapper.RolePermissionMapper;
import com.titancore.domain.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    /**
     *@param loginId：账号id，即你在调用 StpUtil.login(id) 时写入的标识值。
     *@param  loginType：账号体系标识，此处可以暂时忽略，在 [ 多账户认证 ] 章节下会对这个概念做详细的解释。
     * @return
     */
    /** 返回一个账号所拥有的权限码集合
     * explain: titan:article:search
     * */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userid = SaFoxUtil.getValueByType(loginId, Long.class);
        List<Role> roles = userRoleMapper.queryRoleByUserId(userid);
        List<String> allRolesPermissions = new ArrayList<>(); // 创建一个用于保存合并结果的列表
        for (Role role : roles) {
            List<Permission> permissions = rolePermissionMapper.queryPermissionByRoleId(role.getId());
            for (Permission permission : permissions) {
                allRolesPermissions.add(permission.getPermissionKey());
            }
        }
        return allRolesPermissions;
    }

    /** 返回一个账号所拥有的角色标识集合
     * explain: superpower_user/normal_user
     * */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userid = SaFoxUtil.getValueByType(loginId, Long.class);
        List<Role> roles = userRoleMapper.queryRoleByUserId(userid);
        List<String> rolesByRoleKey = new ArrayList<>();
        for (Role role : roles) {
            rolesByRoleKey.add(role.getRoleKey());
        }
        return rolesByRoleKey;
    }
}
