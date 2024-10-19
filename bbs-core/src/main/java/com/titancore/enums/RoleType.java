package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 角色类型
 */
@Getter
@AllArgsConstructor
public enum RoleType implements IEnum<String> {
    SUPERPOWER_USER("superpower_user"),
    ADMIN_USER("admin_user"),
    HIGH_USER("high_user"),
    NORMAL_USER("normal_user"),
    REGISTERED_USER("registered_user");
    private final String value;
    public static RoleType valueOfAll(String value) {
        for (RoleType linkType : RoleType.values()) {
            if(Objects.equals(value,linkType.getValue())){
                return linkType;
            }

        }
        return null;
    }
    public static RoleType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RoleType linkType : RoleType.values()) {
            if (linkType.value.equalsIgnoreCase(value)) {
                return linkType;
            }
        }
        return null;
    }
}
