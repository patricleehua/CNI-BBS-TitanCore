package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 链接类型
 */
@Getter
@AllArgsConstructor
public enum LinkType implements IEnum<String> {
    COVER("cover"),
    AVATAR("avatar"),
    BACKGROUND("background"),
    VIDEO("video"),
    TAG("tag"),
    CATEGORY("category"),
    UNKNOWN("unknown");
    private final String value;
    public static LinkType valueOfAll(String value) {
        for (LinkType linkType : LinkType.values()) {
            if(Objects.equals(value,linkType.getValue())){
                return linkType;
            }

        }
        return null;
    }
    public static LinkType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (LinkType linkType : LinkType.values()) {
            if (linkType.value.equalsIgnoreCase(value)) {
                return linkType;
            }
        }
        return null;
    }
}
