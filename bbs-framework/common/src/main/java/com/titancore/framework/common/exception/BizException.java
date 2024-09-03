package com.titancore.framework.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BizException extends RuntimeException {
    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

    public BizException(BaseExceptionInterface baseExceptionInterface) {
        super(baseExceptionInterface.getErrorMessage()); // 调用父类构造函数，设置异常信息
        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }
}
