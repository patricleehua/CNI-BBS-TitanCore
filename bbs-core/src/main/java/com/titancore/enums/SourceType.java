package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum SourceType implements IEnum<String> {
    USER("user"),
    SYSTEM("system"),
    GROUP("group");
    private final String value;
    public static SourceType valueOfAll(String value) {
        for (SourceType sourceType : SourceType.values()) {
            if(Objects.equals(value,sourceType.getValue())){
                return sourceType;
            }

        }
        return null;
    }
}
