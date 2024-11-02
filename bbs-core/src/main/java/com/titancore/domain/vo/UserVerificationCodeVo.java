package com.titancore.domain.vo;

import lombok.Data;


@Data
public class UserVerificationCodeVo {

    private String phoneNumber;

    private String emailNumber;

    private int temporaryCode;

    private boolean isPassed =false;
}
