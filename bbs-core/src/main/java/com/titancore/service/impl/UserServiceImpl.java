package com.titancore.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.PointsRecordDTO;
import com.titancore.domain.dto.RegisterUserDTO;
import com.titancore.domain.dto.UserLoginDTO;
import com.titancore.domain.entity.User;
import com.titancore.domain.entity.UserInvite;
import com.titancore.domain.mapper.UserInviteMapper;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.mapper.UserRoleMapper;
import com.titancore.domain.vo.PointsRuleVo;
import com.titancore.domain.vo.UserLoginVo;
import com.titancore.domain.vo.UserRegisterVo;
import com.titancore.domain.vo.UserVo;
import com.titancore.enums.*;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.properties.Md5Salt;
import com.titancore.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private Md5Salt md5Salt;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FollowService followService;
    @Autowired
    private UserInviteMapper userInviteMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private PointsRuleService pointsRuleService;
    @Autowired
    private PointsRecordService pointsRecordService;
    @Autowired
    private EmailService emailService;

    public UserLoginVo login(UserLoginDTO userLoginDto) {
        LoginEnum loginType = LoginEnum.valueOfAll(userLoginDto.getLoginType());
        User user = null;
        if (loginType != null) {
            switch (loginType) {
                case PASSWORD -> {
                    //todo 注意bug!!!
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

    @Override
    public String onlineByToken(String token) {
        String userId = stringRedisTemplate.opsForValue().get(RedisConstant.SATOKEN_LOGIN_PRIX + token);
        if(userId !=null ){
            //todo 更改用户在线状态
            return userId;
        }
        return null;
    }
    @Override
    public void offline(String userId) {
        if(userId!=null){
            userMapper.update(new LambdaUpdateWrapper<User>().eq(User::getUserId,userId).set(User::getLoginTime, LocalDateTime.now()));
        }
    }

    @Override
    public List<UserVo> recommendedUserAll() {
        //查找关注度最多的10个用户
        List<Long> userIds = followService.highFollowedTop10();
        if(userIds.isEmpty()){
            return List.of();
        }
        List<User> userList =  userMapper.selectListByIds(userIds);
        return userList.stream().map(user -> userToUserVo(user, true)).toList();
    }
    @Transactional
    @Override
    public UserRegisterVo register(RegisterUserDTO registerUserDTO) {

        //判断账户 是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getLoginName, registerUserDTO.getLoginName()).or()
                .eq(User::getUserName, registerUserDTO.getUserName()).or()
                .eq(User::getPhoneNumber, registerUserDTO.getPhoneNumber()).or()
                .eq(User::getEmail, registerUserDTO.getEmailNumber());
        List<User> existingUsers = userMapper.selectList(queryWrapper);
        if (!existingUsers.isEmpty()) {
            existingUsers.forEach(user -> {
                if (user.getLoginName().equals(registerUserDTO.getLoginName())) {
                    throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_LOGINNAME_IS_ALLERY_EXIST);
                } else if (user.getUserName().equals(registerUserDTO.getUserName())) {
                    throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_USERNAME_IS_ALLERY_EXIST);
                } else if (user.getPhoneNumber().equals(registerUserDTO.getPhoneNumber())) {
                    throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_PHONE_IS_ALLERY_EXIST);
                } else if (user.getEmail().equals(registerUserDTO.getEmailNumber())) {
                    throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_EMAIL_IS_ALLERY_EXIST);
                }
            });
        }
        //分布式锁
        String userRegisterRedisKey = RedisConstant.USER_REGISTER_INFO_PRIX + registerUserDTO.getLoginName();
        String lockKey = RedisConstant.USER_REGISTER_LOCK_PRIX + userRegisterRedisKey;
        String lockValue = UUID.randomUUID().toString();
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 1, TimeUnit.MINUTES))) {
                //新增用户
                User registerUser = new User();
                BeanUtils.copyProperties(registerUserDTO, registerUser);
                //md5加密
                String password = registerUserDTO.getPassword() + md5Salt.getSalt();
                String md5password = DigestUtils.md5DigestAsHex(password.getBytes());
                registerUser.setPassword(md5password);
                registerUser.setUserType("01");
                registerUser.setDelFlag("0");
                registerUser.setSex(String.valueOf(registerUserDTO.getSex()));
                registerUser.setEmail(registerUserDTO.getEmailNumber());
                registerUser.setAvatar("https://profile-avatar.csdnimg.cn/65578c9c4382408eaa4db3d67b4026c4_u010165006.jpg");
                registerUser.setStatus(StatusEnum.DISABLED.getValue().toString());//停用，需要邮箱验证
                //写入数据库
                int userResult = userMapper.insert(registerUser);
                if (!(userResult > 0)) {
                    throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_REGISTER_FAIL);
                }
                //建立权限
                // 生成新的临时ID
                int userRoleRelResult = userRoleMapper.buildUserRoleRelByUserId(new SnowflakeGenerator().next(), registerUser.getUserId(), 5L);
                if (!(userRoleRelResult > 0)) {
                    throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_ROLE_REL_CREATE_FALL);
                }
                //邀请链接
                UserInvite userInvite = null;
                if (StringUtils.isNotEmpty(registerUserDTO.getInviteUrl())) {
                    //查询邀请链接
                    userInvite = userInviteMapper.selectOne(new LambdaQueryWrapper<UserInvite>().eq(UserInvite::getInviteLink, registerUserDTO.getInviteUrl()).last("LIMIT 1"));
                    if (userInvite != null && userInvite.getUserId() != null) {
                        createLink(userInvite.getUserId(), registerUser.getUserId());
                    } else {
                        createLink(registerUser.getUserId());
                    }
                } else {
                    createLink(registerUser.getUserId());
                }

                //注册积分
                PointsRuleVo userRegisterPointsRule = pointsRuleService.queryRuleByUniqueKey(PointsRuleUniqueKey.USER_REGISTER);
                if (userRegisterPointsRule != null) {
                    PointsRecordDTO userRegisterpointsRecordDTO = PointsRecordService.buildPointsRecordDTO(userRegisterPointsRule, String.valueOf(registerUser.getUserId()), PointsStatus.EARNED.getValue());
                    pointsRecordService.addPointsRecord(userRegisterpointsRecordDTO);
                }
                //邀请积分
                if (userInvite != null && userInvite.getUserId() != null) {
                    PointsRuleVo userRegisterIncentive = pointsRuleService.queryRuleByUniqueKey(PointsRuleUniqueKey.USER_REGISTER_INCENTIVE);
                    PointsRecordDTO userRegisterIncentivePointsRecordDTO = PointsRecordService.buildPointsRecordDTO(userRegisterIncentive, String.valueOf(userInvite.getUserId()), PointsStatus.EARNED.getValue());
                    pointsRecordService.addPointsRecord(userRegisterIncentivePointsRecordDTO);

                    PointsRuleVo userFillsRegisterIncentive = pointsRuleService.queryRuleByUniqueKey(PointsRuleUniqueKey.USER_fILLS_REGISTER_INCENTIVE);
                    PointsRecordDTO userFillsRegisterIncentivePointsRecordDTO = PointsRecordService.buildPointsRecordDTO(userFillsRegisterIncentive, String.valueOf(registerUser.getUserId()), PointsStatus.EARNED.getValue());
                    pointsRecordService.addPointsRecord(userFillsRegisterIncentivePointsRecordDTO);
                }
                // todo 邮件通知用户验证账户
                //emailService

                UserRegisterVo userRegisterVo = new UserRegisterVo();
                BeanUtils.copyProperties(registerUser, userRegisterVo);
                userRegisterVo.setUserId(String.valueOf(registerUser.getUserId()));
                return userRegisterVo;
            } else {
                //为获取到锁
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new BizException(ResponseCodeEnum.REDIS_LOCK_KEY_FREE_ERROR);
        } finally {
            try {
                String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
                if (!StringUtils.isEmpty(currentValue) && currentValue.equals(lockValue)) {
                    stringRedisTemplate.delete(lockKey);
                }
            } catch (Exception e) {
                throw new BizException(ResponseCodeEnum.REDIS_LOCK_KEY_FREE_ERROR);
            }
        }
        throw  new BizException(ResponseCodeEnum.AUTH_ACCOUNT_REGISTER_FAIL);
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

    /**
     * User对象转UserVo视图对象
     * @param user
     * @return
     */
    private UserVo userToUserVo(User user,boolean isFollowCount) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.setUserId(String.valueOf(user.getUserId()));
        if(isFollowCount){
            userVo.setFansCount(String.valueOf(followService.followNumCount(user.getUserId(), true)));
            userVo.setFollowingCount(String.valueOf(followService.followNumCount(user.getUserId(), false)));
        }
        return userVo;
    }
    /**
     *
     * 没有填邀请码的邀请表生成
     *
     * @param userid
     */

    private void createLink(Long userid) {
        //生成邀请码
        String linkCode = RandomUtil.randomString(8);
        UserInvite userInvite = new UserInvite();
        userInvite.setUserId(userid);
        userInvite.setInviteLink(linkCode);
        int result = userInviteMapper.insert(userInvite);
        if (!(result > 0)) {
            throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_INVITE_CREATE_FAIL);
        }
    }

    /**
     * 有邀请码的生成邀请表信息
     *
     * @param inviteId
     * @param userid
     */
    private void createLink(Long inviteId, Long userid) {
        String linkCode = RandomUtil.randomString(8);
        UserInvite userInvite = new UserInvite();
        userInvite.setUserId(userid);
        userInvite.setInviteBy(inviteId);
        userInvite.setInviteLink(linkCode);
        int result = userInviteMapper.insert(userInvite);
        if (!(result > 0)) {
            throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_INVITE_CREATE_FAIL);
        }
    }

}

