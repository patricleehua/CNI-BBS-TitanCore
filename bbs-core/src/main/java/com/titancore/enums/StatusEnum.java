package com.titancore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    // 禁用
    DISABLED(0),
    // 启用
    ENABLE(1);

    private final Integer value;
}

