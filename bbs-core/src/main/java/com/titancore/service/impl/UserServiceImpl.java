package com.titancore.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.entity.User;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.enums.LoginEnum;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.StatusEnum;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.properties.Md5Salt;
import com.titancore.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
            switch(loginType) {
                case PASSWORD -> {
                    //   构造查询
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    queryWrapper.and(wrapper1 -> wrapper1
                            .eq("login_name", userLoginDto.getUsername())
                            .or()
                            .eq("phonenumber", userLoginDto.getUsername())  // 添加手机号等于用户名的条件
                            .eq("del_flag","0")
                    );
                     user = getOne(queryWrapper);
                    //账号删除
                    if (user == null) {
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
                    //todo 接入三方接码平台
                    return null;
                }
            }
        }else{
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
                                    .setTimeout(60 * 60 * 24 * 7)
                                    .setIsWriteHeader(true));
                    case NOTREMEMBERME -> StpUtil.login(user.getUserId(),new SaLoginModel()
                            .setIsLastingCookie(false)
                            .setTimeout(60 * 60 )
                            .setIsWriteHeader(true));
                }
            }
        }




        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
       //登入成功创建jwt令牌
        String token = tokenInfo.getTokenValue();
        // 获取：当前账号所拥有的角色集合
        List<String> roleList = StpUtil.getRoleList();
        log.info("roleList：{}",roleList);
        // 获取：当前账号所拥有的权限集合
        List<String> permissionList = StpUtil.getPermissionList();
        log.info("permissionList：{}",permissionList);
        return UserLoginVo.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .token(token)
                .avatar(user.getAvatar())
                .roles(roleList)
                .build();
    }

}

