package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum  MessageType  implements IEnum<String> {

    TEXT("text"),
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio");

    private final String value;
    public static MessageType valueOfAll(String value) {
        for (MessageType messageType : MessageType.values()) {
            if(Objects.equals(value,messageType.getValue())){
                return messageType;
            }

        }
        return null;
    }
    public static MessageType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (MessageType  messageType : MessageType.values()) {
            if (messageType.value.equalsIgnoreCase(value)) {
                return messageType;
            }
        }
        return null;
    }
}
