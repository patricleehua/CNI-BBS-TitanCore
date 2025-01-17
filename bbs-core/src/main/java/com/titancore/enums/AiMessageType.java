package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * AI消息类型
 */
@Getter
@AllArgsConstructor
public enum AiMessageType implements IEnum<String> {

    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system");
    private final String value;
    public static AiMessageType valueOfAll(String value) {
        for (AiMessageType aiMessageType : AiMessageType.values()) {
            if(Objects.equals(value,aiMessageType.getValue())){
                return aiMessageType;
            }

        }
        return null;
    }
}
