package com.titancore.enums;

import com.titancore.framework.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("AUTH-10000", "出错啦，正在努力修复中..."),
    PARAM_NOT_VALID("AUTH-10001", "参数错误"),
    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-20000", "请求太频繁，请3分钟后再试"),
    VERIFICATION_CODE_ERROR("AUTH-20001", "验证码错误"),
    ACCOUNT_DELETE("AUTH-30001","账号已被删除,请联系论坛管理员！"),
    PASSWORD_ERROR("AUTH-30002","密码错误"),
    ACCOUNT_LOCKED("AUTH-30003","账号被锁定"),
    ACCOUNT_VERIFICATION_TYPE_ISNULL("AUTH-30004","登入验证类型不能为空"),
    CAPTCHACODE_IS_ERROR("AUTH-30005","验证码发送失败"),
    PHONE_NUMBER_IS_ERROR("AUTH-30006","输入的手机号码有误"),
    EMAIL_NUMBER_IS_ERROR("AUTH-30007","输入的邮箱号码有误"),
    APTCHACODE_ISNOTSAME("AUTH-30008","输入的验证码有误"),
    APTCHACODE_IS_NOT_EXIST("AUTH-30009","验证码不存在"),

    AUTH_ACCOUNT_IS_DIFFERENT("AUTH-30010","操作者与当前线程用户不一致!"),
    AUTH_ACCOUNT_IS_MISSED("AUTH-30011","操作者信息丢失或有误!"),

    UPLOAD_FILE_IS_TOO_BIG("FILE-10001","上传的文件过大"),
    FILE_IS_NOT_IMAGE("FILE-10002", "上传的文件格式须为图片"),
    FILE_IS_NOT_VIDEO("FILE-10003", "上传的文件格式须为视频"),
    FILE_IS_NOT_SUPPORTED("FILE-10004", "上传的文件格式不支持"),

    //Category
    CATEGORY_CATEGORYID_CAN_NOT_BE_NULL("CATEGORY-10001","板块ID不能为空!"),
    CATEGORY_CATEGORYNAME_IS_EXIST("CATEGORY-10002","板块名称已存在!" ),
    CATEGORY_CATEGORYID_IS_NOT_EXIST("CATEGORY-10003","板块ID不存在!" ),
    //Tag
    TAG_TAGID_CAN_NOT_BE_NULL("TAG-10001","标签ID不能为空!"),
    TAG_TAGNAME_IS_EXIST("TAG-10002","标签名称已存在!" ),
    TAG_TAGID_IS_NOT_EXIST("TAG-10003","标签ID不存在!" );

    // ----------- 业务异常状态码 -----------

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

    // ----------- 业务异常状态码 -----------
}
