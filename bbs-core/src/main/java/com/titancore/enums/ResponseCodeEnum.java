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
    ACCOUNT_CAN_NOT_FOUND("AUTH-30008","账号没有找到！"),


    AUTH_ACCOUNT_IS_DIFFERENT("AUTH-30010","操作者与当前线程用户不一致!"),
    AUTH_ACCOUNT_IS_MISSED("AUTH-30011","操作者信息丢失或有误!"),

    AUTH_ACCOUNT_LOGINNAME_IS_ALLERY_EXIST("AUTH-30012","用户登入名已存在,不能重复!"),
    AUTH_ACCOUNT_USERNAME_IS_ALLERY_EXIST("AUTH-30012","用户名已存在,不能重复!"),
    AUTH_ACCOUNT_EMAIL_IS_ALLERY_EXIST("AUTH-30013","用户邮箱已存在,不能重复!"),
    AUTH_ACCOUNT_PHONE_IS_ALLERY_EXIST("AUTH-30014","用户手机号已存在,不能重复!"),

    AUTH_ACCOUNT_REGISTER_FAIL("AUTH-30015","用户注册失败!" ),
    AUTH_ACCOUNT_ROLE_REL_CREATE_FALL("AUTH-30016","用户角色关系构建失败!" ),
    AUTH_ACCOUNT_INVITE_CREATE_FAIL("AUTH-30017","用户链接创建失败!" ),
    AUTH_ACCOUNT_TEMPORARY_USER_CREATED_FAIL("AUTH-30018","临时用户创建失败!" ),

    //code
    APTCHACODE_IS_NOT_EXIST("APTCHACODE-10001","验证码不存在!"),
    APTCHACODE_IS_NOT_CORRECT("APTCHACODE-10002","验证码校验不通过!"),
    //passcode
    PASSCODE_IS_NOT_EXIST("PASSCODE-10001","输入的通行码不存在!"),
    PASSCODE_IS_NOT_CORRECT("PASSCODE-10001","输入的通行码校验不通过!"),

    UPLOAD_FILE_IS_TOO_BIG("FILE-10001","上传的文件过大"),
    FILE_IS_NOT_IMAGE("FILE-10002", "上传的文件格式须为图片"),
    FILE_IS_NOT_VIDEO("FILE-10003", "上传的文件格式须为视频"),
    FILE_IS_NOT_SUPPORTED("FILE-10004", "上传的文件格式不支持"),
    FILE_IS_NOT_MATCH("FILE-10005", "上传的文件不一致!"),
    //Redis
    REDIS_LOCK_KEY_FREE_ERROR("REDIS-10001","分布式锁释放失败"),

    //Category
    CATEGORY_CATEGORYID_CAN_NOT_BE_NULL("CATEGORY-10001","板块ID不能为空!"),
    CATEGORY_CATEGORYNAME_IS_EXIST("CATEGORY-10002","板块名称已存在!" ),
    CATEGORY_CATEGORYID_IS_NOT_EXIST("CATEGORY-10003","板块ID不存在!" ),
    //Tag
    TAG_TAGID_CAN_NOT_BE_NULL("TAG-10001","标签ID不能为空!"),
    TAG_TAGNAME_IS_EXIST("TAG-10002","标签名称已存在!" ),
    TAG_TAGID_IS_NOT_EXIST("TAG-10003","标签ID不存在!" ),

    //Follow
    FOLLOW_STATUS_ERROR("FOLLOW-10001","关注状态有误！"),
    FOLLOW_STRUCTURE_IS_EXIST("FOLLOW-10002","关注结构已存在!"),

    //Points
    POINTS_RULE_IS_NULL("POINTS-10001", "查无此积分规则!"),
    POINTS_RULE_IS_NOT_ACTIVE("POINTS-10002", "积分规则已失效!"),
    POINTS_IS_NOT_MATCH("POINTS-10003", "积分数量与积分规则表不一致!"),

    //MessageQueue
    MESSAGE_QUEUE_ERROR("MESSAGE-10001","消息队列异常"),
    MESSAGE_QUEUE_SEND_MESSAGE_TO_USER_ERROR("MESSAGE-10002","发送用户消息到消息队列出错!"),


    //ChatMessage
    CHAT_MESSAGE_CONTENT_IS_NOT_EXIST("CHAT-10001","消息不存在!"),
    ;

    // ----------- 业务异常状态码 -----------

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

    // ----------- 业务异常状态码 -----------
}
