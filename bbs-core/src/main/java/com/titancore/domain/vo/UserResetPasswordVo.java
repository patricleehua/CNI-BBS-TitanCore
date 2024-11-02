package com.titancore.domain.vo;

import lombok.Data;


@Data
public class UserResetPasswordVo {

    private String phoneNumber;

    private String emailNumber;

    private boolean result;
}
