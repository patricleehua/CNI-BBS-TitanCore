package com.titancore.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.entity.User;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.enums.CapchaEnum;
import com.titancore.enums.LoginEnum;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.StatusEnum;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.properties.Md5Salt;
import com.titancore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private Md5Salt md5Salt;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public UserLoginVo login(UserLoginDTO userLoginDto) {
        LoginEnum loginType = LoginEnum.valueOfAll(userLoginDto.getLoginType());
        User user = null;
        if (loginType != null) {
            switch (loginType) {
                case PASSWORD -> {
                    //todo 注意bug
                    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                            .eq(User::getDelFlag, "0")
                            .and(w -> w.eq(User::getLoginName, userLoginDto.getUsername())
                                    .or().eq(User::getPhoneNumber, userLoginDto.getPhoneNumber())
                                    .or().eq(User::getEmail, userLoginDto.getEmailNumber()))
                            .last("limit 1");
                    user = userMapper.selectOne(queryWrapper);
                    //账号异常
                    if (user == null ) {
                        throw new BizException(ResponseCodeEnum.ACCOUNT_DELETE);
                    }
                    //md5加密
                    String password = userLoginDto.getPassword() + md5Salt.getSalt();
                    String md5password = DigestUtils.md5DigestAsHex(password.getBytes());

                    //密码
                    if (!md5password.equals(user.getPassword())) {
                        throw new BizException(ResponseCodeEnum.PASSWORD_ERROR);
                    }
                    //状态
                    String status = String.valueOf(StatusEnum.ENABLE.getValue());
                    if (!user.getStatus().equals(status)) {
                        throw new BizException(ResponseCodeEnum.ACCOUNT_LOCKED);
                    }
                }
                case VERIFICATION_CODE -> {
                    String code = getCaptchaRedisCache(userLoginDto);
                    if (!code.equals(userLoginDto.getCaptchaCode())) {
                        throw new BizException(ResponseCodeEnum.APTCHACODE_ISNOTSAME);
                    }

                    user = getOne(new LambdaQueryWrapper<User>()
                            .eq(User::getPhoneNumber, userLoginDto.getPhoneNumber())
                            .or()
                            .eq(User::getEmail, userLoginDto.getEmailNumber())
                            .eq(User::getDelFlag, "0"));
                }
            }
        } else {
            throw new BizException(ResponseCodeEnum.ACCOUNT_VERIFICATION_TYPE_ISNULL);
        }

        //todo
        if (user != null) {
            //登入成功创建saToken
            LoginEnum rememberMe = LoginEnum.valueOfAll(userLoginDto.getRememberMe());

            if (rememberMe != null) {
                switch (rememberMe) {
                    case REMEMBERME -> StpUtil.login(user.getUserId(),
                            new SaLoginModel()
                                    .setIsLastingCookie(true)
                                    .setTimeout(60 * 60 * 24 * 7)//7 days
                                    .setIsWriteHeader(true));
                    case NOTREMEMBERME -> StpUtil.login(user.getUserId(), new SaLoginModel()
                            .setIsLastingCookie(false)
                            .setTimeout(60 * 60)//1 hour
                            .setIsWriteHeader(true));
                }
            }
            delCaptchaRedisCache(userLoginDto);
        }
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        //登入成功创建jwt令牌
        String token = tokenInfo.getTokenValue();
        // 获取：当前账号所拥有的角色集合
        List<String> roleList = StpUtil.getRoleList();
        log.info("roleList：{}", roleList);
        // 获取：当前账号所拥有的权限集合
        List<String> permissionList = StpUtil.getPermissionList();
        log.info("permissionList：{}", permissionList);
        return UserLoginVo.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .token(token)
                .avatar(user.getAvatar())
                .roles(roleList)
                .build();
    }

    /**
     * 提取redis缓存的的验证码比对
     * @param userLoginDto
     * @return 是否已发送
     */
    private String getCaptchaRedisCache(UserLoginDTO userLoginDto) {
        CapchaEnum capchaType = CapchaEnum.valueOfAll(userLoginDto.getCaptchaType());
        String res = null;
        if (capchaType != null) {
            String redisKey = null;
            switch (capchaType) {
                case CAPTCHA_TYPE_PHONE -> redisKey = RedisConstant.PHONE_PRIX + userLoginDto.getPhoneNumber();
                case CAPTCHA_TYPE_EMAIL -> redisKey = RedisConstant.EMAIL_PRIX + userLoginDto.getEmailNumber();
            }
             res = stringRedisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isEmpty(res)) {
                throw new BizException(ResponseCodeEnum.APTCHACODE_IS_NOT_EXIST);
            }
            return res;
        }
        return res;
    }
    /**
     * 使用后清除redis缓存的的验证码
     * @param userLoginDto
     * @return
     */
    private void delCaptchaRedisCache(UserLoginDTO userLoginDto) {
        CapchaEnum capchaType = CapchaEnum.valueOfAll(userLoginDto.getCaptchaType());
        if (capchaType != null) {
            String redisKey = null;
            switch (capchaType) {
                case CAPTCHA_TYPE_PHONE -> redisKey = RedisConstant.PHONE_PRIX + userLoginDto.getPhoneNumber();
                case CAPTCHA_TYPE_EMAIL -> redisKey = RedisConstant.EMAIL_PRIX + userLoginDto.getEmailNumber();
            }
            Boolean delete = stringRedisTemplate.delete(redisKey);
        }
    }
}

