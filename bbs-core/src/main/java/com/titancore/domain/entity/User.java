package com.titancore.domain.entity;

import java.util.*;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@SuppressWarnings("serial")
@Data
@TableName("user")
public class User  {
    //用户ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;
    //登录账号
    private String loginName;
    //这个是mybatis-plus需要用到的方法
    //用户昵称
    private String userName;
    //用户类型（00系统用户 01注册用户）
    private String userType;
    //用户邮箱
    private String email;
    //手机号码
    private String phonenumber;
    //用户性别（0男 1女 2未知）
    private String sex;
    //头像路径
    private String avatar;
    //密码
    private String password;
    //帐号状态（0正常 1停用）
    private String status;
    //删除标志（0代表存在 2代表删除）
    private String delFlag;
    //最后登录IP
    private String loginIp;
    //最后登录时间
    private Long loginDate;
    //密码最后更新时间
    private Long pwdUpdateDate;
    //创建者
    private String createBy;
    //创建时间
    private Long createTime;
    //更新者
    private String updateBy;
    //更新时间
    private Long updateTime;
    //备注
    private String remark;
    @TableField(exist = false)
    //角色对象
    private List<Role> roles;

}

