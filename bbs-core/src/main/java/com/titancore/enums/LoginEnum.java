package com.titancore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum LoginEnum {

    VERIFICATION_CODE("v_code"),
    PASSWORD("password"),
    REMEMBERME("0"),
    NOTREMEMBERME("1");

    private final String value;


    public static LoginEnum valueOfAll(String value) {
        for (LoginEnum loginEnum : LoginEnum.values()) {
            if(Objects.equals(value,loginEnum.getValue())){
                return loginEnum;
            }

        }
        return null;
    }
}

