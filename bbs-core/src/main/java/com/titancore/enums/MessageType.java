package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 消息类型
 */
@Getter
@AllArgsConstructor
public enum MessageType implements IEnum<String> {

    MESSAGE("message"),
    NOTIFY("notify"),
    MEDIA("media");
    private final String value;
    public static MessageType valueOfAll(String value) {
        for (MessageType levelEnum : MessageType.values()) {
            if(Objects.equals(value,levelEnum.getValue())){
                return levelEnum;
            }

        }
        return null;
    }
}
