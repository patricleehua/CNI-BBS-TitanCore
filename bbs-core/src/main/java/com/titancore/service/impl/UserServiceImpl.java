package com.titancore.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.entity.User;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.StatusEnum;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.properties.Md5Salt;
import com.titancore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * 用户信息表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-11-22 18:16:49
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private Md5Salt md5Salt;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public UserLoginVo login(UserLoginDTO userLoginDto) {
        //使用security重写了验证流程
     //   构造查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper1 -> wrapper1
                        .eq("login_name", userLoginDto.getUsername())
                        .or()
                        .eq("phonenumber", userLoginDto.getUsername())  // 添加手机号等于用户名的条件
                        .eq("del_flag","0")
        );
        User user = getOne(queryWrapper);
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

        //todo
        //登入成功创建satoken
        StpUtil.login(user.getUserId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
       //登入成功创建jwt令牌
        String token = tokenInfo.getTokenValue();
        //查询权限
        List<String> roleList = StpUtil.getRoleList();
        return UserLoginVo.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .token(token)
                .avatar(user.getAvatar())
                .role(roleList)
                .build();
    }

}

