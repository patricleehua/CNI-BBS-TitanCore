package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AuthSourceType implements IEnum<String> {
    GITEE("gitee"),
    GOOGLE("google"),
    WECHAT_OPEN("wechat_open");
    private final String value;
    public static AuthSourceType valueOfAll(String value) {
        for (AuthSourceType authSourceType : AuthSourceType.values()) {
            if(Objects.equals(value,authSourceType.getValue())){
                return authSourceType;
            }

        }
        return null;
    }

    public static AuthSourceType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (AuthSourceType authSourceType : AuthSourceType.values()) {
            if (authSourceType.value.equalsIgnoreCase(value)) {
                return authSourceType;
            }
        }
        return null;
    }
}
