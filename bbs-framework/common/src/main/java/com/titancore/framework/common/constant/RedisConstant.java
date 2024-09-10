package com.titancore.framework.common.constant;

public class RedisConstant {
    //注册验证码前缀
    public final static String REGISTER_PHONE_PRIX="register:phone:";
    //验证码前缀
    public final static String PHONE_PRIX="normal:phone:";
    //验证码保存时间
    public final static Long PHONE_TTL = 5L;

    //注册验证码邮箱前缀
    public final static String REGISTER_EMAIL_PRIX="register:email:";
    //验证码邮箱前缀
    public final static String EMAIL_PRIX="normal:email:";
    //邮箱验证码保存时间
    public final static Long EMAIL_TTL = 5L;


}
