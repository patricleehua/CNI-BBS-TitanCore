package com.titancore.domain.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_role")
public class UserRole {
    private Long userId;
    /**
     * 用户id
     */
    private Long roleId;

}
