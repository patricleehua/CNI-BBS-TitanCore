package com.titancore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CapchaEnum {

    CAPTCHA_TYPE_EMAIL("email"),
    CAPTCHA_TYPE_PHONE("phone");


    private final String value;


    public static CapchaEnum valueOfAll(String value) {
        for (CapchaEnum capchaEnum : CapchaEnum.values()) {
            if(Objects.equals(value,capchaEnum.getValue())){
                return capchaEnum;
            }

        }
        return null;
    }
}

