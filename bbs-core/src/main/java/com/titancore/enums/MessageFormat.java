package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 消息格式
 */
@Getter
@AllArgsConstructor
public enum MessageFormat implements IEnum<String> {

    TEXT("text"),
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    FILE("file");

    private final String value;
    public static MessageFormat valueOfAll(String value) {
        for (MessageFormat messageFormat : MessageFormat.values()) {
            if(Objects.equals(value, messageFormat.getValue())){
                return messageFormat;
            }

        }
        return null;
    }
    public static MessageFormat fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (MessageFormat messageFormat : MessageFormat.values()) {
            if (messageFormat.value.equalsIgnoreCase(value)) {
                return messageFormat;
            }
        }
        return null;
    }
}
