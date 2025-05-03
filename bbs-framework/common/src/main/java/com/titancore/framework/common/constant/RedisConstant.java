package com.titancore.framework.common.constant;

public class RedisConstant {
    /**
     * 注册验证码前缀
     */
    public final static String REGISTER_PHONE_PRIX="register:phone:";
    /**
     * 验证码前缀
     */
    public final static String PHONE_PRIX="normal:phone:";
    /**
     * 验证码保存时间
     */
    public final static Long PHONE_TTL = 5L;
    /**
     * 注册验证码邮箱前缀
     */
    public final static String REGISTER_EMAIL_PRIX="register:email:";
    /**
     * 验证码邮箱前缀
     */
    public final static String EMAIL_PRIX="normal:email:";
    /**
     * 邮箱验证码保存时间
     */
    public final static Long EMAIL_TTL = 5L;

    /**
     * 临时帖子ID前缀
     */
    public final static String TEMPORARYPOSTID_PRIX="temporary:post:postId:user:";
    /**
     * 临时帖子ID保存时间
     */
    public final static Long TEMPORARYPOSTID_TTL = 1L;
    /**
     * 临时帖子媒体url列表前缀
     */
    public static final String TEMPORARYPOSTMEDIA_PRIX = "temporary:post:listurl:";
    /**
     * 临时帖子媒体url列表过期时间
     */
    public static final Long TEMPORARYPOSTMEDIA_TTL = 1L;
    /**
     * SaToken保存的toekn路径前缀
     */
    public static final String SATOKEN_LOGIN_PRIX = "token:login:token:";
    /**
     * 用户注册分布式锁前缀
     */
    public static final String USER_REGISTER_LOCK_PRIX = "user_register_lock:";
    /**
     * 用户注册重要信息标识前缀
     */
    public static final String USER_REGISTER_INFO_PRIX = "user:register:info:";
    /**
     * 临时通行码前缀 (仅一次使用)
     */
    public static final String TEMPORARY_PASS_CODE_PRIX = "temporary:pass:code:";
    /**
     * 临时通行码过期时间
     */
    public static final Long TEMPORARY_PASS_CODE_TTL = 5L;
    /**
     * 消息媒体临时链接
     */
    public static final String MESSAGE_MEDIA_TEMPORARY_URL = "message:media:temporary:url:";
    /**
     * 消息媒体临时链接时长
     */
    public static final Long MESSAGE_MEDIA_TEMPORARY_URL_TTL = 7L;
    /**
     * 帖子更新分布式锁前缀
     */
    public static final String POST_UPDATE_LOCK_PRIX = "post_update_lock:";

    public static final String PASSWORD_ERROR_PRIX = "password:error:";
    public static final long PASSWORD_ERROR_TTL = 5L;
    ;
}
