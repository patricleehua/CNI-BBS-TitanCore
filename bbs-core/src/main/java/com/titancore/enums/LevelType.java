package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 消息级别
 */
@Getter
@AllArgsConstructor
public enum LevelType implements IEnum<String> {

    SYSTEM("system"),
    NORMAL("normal");
    private final String value;
    public static LevelType valueOfAll(String value) {
        for (LevelType levelEnum : LevelType.values()) {
            if(Objects.equals(value,levelEnum.getValue())){
                return levelEnum;
            }

        }
        return null;
    }
}
