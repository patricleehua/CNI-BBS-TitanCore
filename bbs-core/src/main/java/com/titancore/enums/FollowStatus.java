package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 关注状态
 */
@Getter
@AllArgsConstructor
public enum FollowStatus implements IEnum<String> {

    PENDING("pending"),
    CONFIRMED("confirmed"),
    REJECTED("rejected"),
    NONE("none");
    private final String value;
    public static FollowStatus valueOfAll(String value) {
        for (FollowStatus followStatus : FollowStatus.values()) {
            if(Objects.equals(value,followStatus.getValue())){
                return followStatus;
            }

        }
        return null;
    }
}
