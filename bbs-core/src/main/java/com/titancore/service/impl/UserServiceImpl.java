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
import com.titancore.constant.PointsRuleUniqueKey;
import com.titancore.domain.dto.*;
import com.titancore.domain.entity.User;
import com.titancore.domain.entity.UserInvite;
import com.titancore.domain.mapper.UserInviteMapper;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.mapper.UserRoleMapper;
import com.titancore.domain.vo.*;
import com.titancore.enums.*;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.properties.Md5Salt;
import com.titancore.service.*;
import com.titancore.util.AuthenticationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final Md5Salt md5Salt;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final FollowService followService;
    private final UserInviteMapper userInviteMapper;
    private final UserRoleMapper userRoleMapper;
    private final PointsRuleService pointsRuleService;
    private final PointsRecordService pointsRecordService;
    private final EmailService emailService;
    private final SocialUserService socialUserService;

    public UserLoginVo login(UserLoginDTO userLoginDto) {
        LoginEnum loginType = LoginEnum.valueOfAll(userLoginDto.getLoginType());
        User user = null;
        if (loginType != null) {
            switch (loginType) {
                case PASSWORD -> {
                    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                            .eq(User::getDelFlag, "0")
                            .last("limit 1");
                    if (!userLoginDto.getUsername().isEmpty()){
                        queryWrapper.eq(User::getLoginName, userLoginDto.getUsername());
                    }else if(!userLoginDto.getEmailNumber().isEmpty()){
                        queryWrapper.eq(User::getEmail, userLoginDto.getEmailNumber());
                    }else if(!userLoginDto.getPhoneNumber().isEmpty()){
                        queryWrapper.eq(User::getPhoneNumber, userLoginDto.getPhoneNumber());
                    }
                    user = userMapper.selectOne(queryWrapper);
                    //账号异常
                    if (user == null ) {
                        throw new BizException(ResponseCodeEnum.ACCOUNT_DELETE);
                    }
                    // 错误计数 Key
                    String redisKey = RedisConstant.PASSWORD_ERROR_PRIX + user.getUserId();
                    // 判断当前错误次数
                    String errorStr = stringRedisTemplate.opsForValue().get(redisKey);
                    int errorCount = errorStr == null ? 0 : Integer.parseInt(errorStr);

                    // 如果已经达到或超过 5 次，直接抛异常（**关键：不要等到 INCR 后再判断**）
                    if (errorCount >= 5) {
                        throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_PASSWORD_ERROR_COUNT);
                    }
                    // 密码加密比对
                    String password = userLoginDto.getPassword() + md5Salt.getSalt();
                    String md5password = DigestUtils.md5DigestAsHex(password.getBytes());

                    if (!md5password.equals(user.getPassword())) {
                        // 密码错误，原子递增
                        Long newErrorCount = stringRedisTemplate.opsForValue().increment(redisKey);

                        // 第一次错误设置过期时间
                        if (newErrorCount != null && newErrorCount == 1) {
                            stringRedisTemplate.expire(redisKey, RedisConstant.PASSWORD_ERROR_TTL, TimeUnit.MINUTES);
                        }

                        // 再次检查：如果递增之后超过限制，也抛异常
                        if (newErrorCount != null && newErrorCount >= 5) {
                            throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_PASSWORD_ERROR_COUNT);
                        }

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
                        throw new BizException(ResponseCodeEnum.APTCHACODE_IS_NOT_CORRECT);
                    }
                    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                            .eq(User::getDelFlag, "0")
                            .last("limit 1");
                    if (!userLoginDto.getEmailNumber().isEmpty()){
                        queryWrapper.eq(User::getEmail, userLoginDto.getEmailNumber());
                    }else if(!userLoginDto.getPhoneNumber().isEmpty()){
                        queryWrapper.eq(User::getPhoneNumber, userLoginDto.getPhoneNumber());
                    }
                    user = getOne(queryWrapper);
                }
                case TEMPORARYCODE ->{
                    boolean isVerify = verifyTemporaryPassCode(userLoginDto.getEmailNumber() != null ? userLoginDto.getEmailNumber() : userLoginDto.getPhoneNumber(), userLoginDto.getCaptchaCode());
                    if(isVerify){
                        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                                .eq(User::getDelFlag, "0")
                                .last("limit 1");
                        if (!userLoginDto.getEmailNumber().isEmpty()){
                            queryWrapper.eq(User::getEmail, userLoginDto.getEmailNumber());
                        }else if(!userLoginDto.getPhoneNumber().isEmpty()){
                            queryWrapper.eq(User::getPhoneNumber, userLoginDto.getPhoneNumber());
                        }
                        user = getOne(queryWrapper);
                    }else{
                        throw new BizException(ResponseCodeEnum.PASSCODE_IS_NOT_CORRECT);
                    }
                }
            }
        } else {
            throw new BizException(ResponseCodeEnum.ACCOUNT_VERIFICATION_TYPE_ISNULL);
        }

        //todo 待完善部分代码
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
    public boolean checkUserIfExist(String account) {
        //todo 第三方登入待处理
        if(account == null){
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, account)
                .or().eq(User::getPhoneNumber, account)
                .or().eq(User::getLoginName, account)
                .or().eq(User::getUserName, account));
        return !users.isEmpty();
    }

    @Override
    public UserVerificationCodeVo checkUserVerificationCode(VerificationCodeForUserDTO verificationCodeForUserDTO) {
        String emailNumber = verificationCodeForUserDTO.getEmailNumber();
        String phoneNumber = verificationCodeForUserDTO.getPhoneNumber();
        String code = verificationCodeForUserDTO.getCode();
        UserVerificationCodeVo userVerificationCodeVo = new UserVerificationCodeVo();

        if (emailNumber == null && phoneNumber == null) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        if (emailNumber != null) {
            String redisKey = RedisConstant.EMAIL_PRIX + emailNumber;
            String emailCode = stringRedisTemplate.opsForValue().get(redisKey);
            if (emailCode == null) {
                throw new BizException(ResponseCodeEnum.APTCHACODE_IS_NOT_EXIST);
            }
            if (!emailCode.equals(code)) {
                throw new BizException(ResponseCodeEnum.APTCHACODE_IS_NOT_CORRECT);
            }
            stringRedisTemplate.delete(redisKey);
            userVerificationCodeVo.setEmailNumber(emailNumber);
        }else {
            String redisKey = RedisConstant.PHONE_PRIX + phoneNumber;
            String phoneCode = stringRedisTemplate.opsForValue().get(redisKey);
            if (phoneCode == null) {
                throw new BizException(ResponseCodeEnum.APTCHACODE_IS_NOT_EXIST);
            }
            if (!phoneCode.equals(code)) {
                throw new BizException(ResponseCodeEnum.APTCHACODE_IS_NOT_CORRECT);
            }
            stringRedisTemplate.delete(redisKey);
            userVerificationCodeVo.setPhoneNumber(phoneNumber);
        }

        String key = emailNumber != null
                ? emailNumber
                : phoneNumber;
        int temporaryPassCode = getTemporaryPassCode(key);
        userVerificationCodeVo.setTemporaryCode(temporaryPassCode);
        userVerificationCodeVo.setPassed(true);
        return userVerificationCodeVo;
    }
    @Override
    public int getTemporaryPassCode(String key) {
        String redisKey = RedisConstant.TEMPORARY_PASS_CODE_PRIX + key;
        int temporaryPassCode = RandomUtil.randomInt(100000, 1000000);
        stringRedisTemplate.opsForValue().set(
                redisKey,
                String.valueOf(temporaryPassCode),
                RedisConstant.TEMPORARY_PASS_CODE_TTL,
                TimeUnit.MINUTES
        );
        return temporaryPassCode;
    }
    @Override
    public boolean verifyTemporaryPassCode(String key,String verifyCode){
        String redisKey = RedisConstant.TEMPORARY_PASS_CODE_PRIX + key;
        String temporaryPassCode = stringRedisTemplate.opsForValue().get(redisKey);
        if(temporaryPassCode == null){
            //清除通行码
            stringRedisTemplate.delete(redisKey);
            throw new  BizException(ResponseCodeEnum.PASSCODE_IS_NOT_EXIST);
        }
        if(!temporaryPassCode.equals(verifyCode)){
            throw new BizException(ResponseCodeEnum.PASSCODE_IS_NOT_CORRECT);
        }
        return true;
    }
    @Transactional
    @Override
    public DMLVo socialUserBindLocalUser(BindSocialUserDTO bindSocialUserDTO) {
        String phoneNumber = bindSocialUserDTO.getPhoneNumber();
        String emailNumber = bindSocialUserDTO.getEmailNumber();
        if (StringUtils.isEmpty(phoneNumber) && StringUtils.isEmpty(emailNumber)) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        User user = null;
        if(bindSocialUserDTO.getHasLocalUser()){
            if (StringUtils.isNotEmpty(phoneNumber)) {
                user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhoneNumber, phoneNumber));
            } else if (StringUtils.isNotEmpty(emailNumber)) {
                user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, emailNumber));
            }
        }else{
            int count = socialUserService.countBindUser(bindSocialUserDTO.getSocialUserId());
            if (count > 0) {
                throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_HAS_BINDED);
            }
            user = socialUserService.buildThirdUserFromSocialUser(bindSocialUserDTO.getSocialUserId());
            //生成临时密码随机
            String temporaryPassword = bindSocialUserDTO.getPassword().isEmpty() ? RandomUtil.randomString(10) : bindSocialUserDTO.getPassword();
            temporaryPassword += md5Salt.getSalt();
            String md5temporaryPassword = DigestUtils.md5DigestAsHex(temporaryPassword.getBytes());
            user.setPassword(md5temporaryPassword);
            if (StringUtils.isNotEmpty(phoneNumber)) {
                user.setPhoneNumber(phoneNumber);
            } else if (StringUtils.isNotEmpty(emailNumber)) {
                user.setEmail(emailNumber);
            }
            int userResult = userMapper.insert(user);
            if (!(userResult > 0)) {
                throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_TEMPORARY_USER_CREATED_FAIL);
            }
            // 生成新的临时ID
            int userRoleRelResult = userRoleMapper.buildUserRoleRelByUserId(new SnowflakeGenerator().next(), user.getUserId(), 6L);
            if (!(userRoleRelResult > 0)) {
                throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_ROLE_REL_CREATE_FALL);
            }
        }

        if (user == null) {
            throw new BizException(ResponseCodeEnum.ACCOUNT_CAN_NOT_FOUND);
        }
        Long snowflakeId = new SnowflakeGenerator().next();
        int result = socialUserService.buildRelForSocialUserWithUserByUserIdAndSocialUserId(snowflakeId, user.getUserId(), bindSocialUserDTO.getSocialUserId());

        DMLVo dmlVo = new DMLVo();
        if (result > 0){
            dmlVo.setId(String.valueOf(snowflakeId));
            dmlVo.setStatus(true);
            StpUtil.login(user.getUserId());
            String token = StpUtil.getTokenValue();
            dmlVo.setMessage(token);
        }else {
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_CREATE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public UserVo findUserInfoByUserId(Long userId) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if (user == null){
            return null;
        }
        return userToUserVo(user,false,null);
    }

    @Override
    public UserLoginVo getUserInfoByToken(long userId) {
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        String token = tokenInfo.getTokenValue();
        List<String> roleList = StpUtil.getRoleList();
        User user = userMapper.selectById(userId);
        List<String> permissionList = StpUtil.getPermissionList();
        return UserLoginVo.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .token(token)
                .avatar(user.getAvatar())
                .roles(roleList)
                .build();
    }

    public List<UserVo> recommendedUserByUserId(String userId) {
        AuthenticationUtil.checkUserId(userId);
        //查找关注度最多的10个用户
        List<Long> userIds = followService.highFollowedTop10();
        if(userIds.isEmpty()){
            return List.of();
        }
        List<User> userList =  userMapper.selectListByIds(userIds);
        return userList.stream().map(user -> userToUserVo(user, true,userId)).toList();
    }


    @Override
    public UserResetPasswordVo resetPassword(ResetPasswordUserDTO resetPasswordUserDTO) {
        UserResetPasswordVo userResetPasswordVo = new UserResetPasswordVo();
        String emailNumber = resetPasswordUserDTO.getEmailNumber();
        String phoneNumber = resetPasswordUserDTO.getPhoneNumber();
        String code = resetPasswordUserDTO.getTemporaryCode();
        String newPassword = resetPasswordUserDTO.getNewPassword();
        if (emailNumber == null && phoneNumber == null) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        //校验临时通行码
        verifyTemporaryPassCode(emailNumber != null ? emailNumber : phoneNumber, code);

        int result = 0;
        if(!newPassword.isEmpty()){
            newPassword += md5Salt.getSalt();
            String md5newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
            if(emailNumber != null && !emailNumber.isEmpty()){
                userResetPasswordVo.setEmailNumber(emailNumber);
                result = userMapper.update(new LambdaUpdateWrapper<User>().eq(User::getEmail, emailNumber).set(User::getPassword, md5newPassword));
            }else {
                userResetPasswordVo.setPhoneNumber(phoneNumber);
                result = userMapper.update(new LambdaUpdateWrapper<User>().eq(User::getPhoneNumber, phoneNumber).set(User::getPassword, md5newPassword));
            }
        }
        userResetPasswordVo.setResult(result > 0);
        return userResetPasswordVo;
    }

    @Override
    public List<UserVo> recommendedUserAll() {
        //查找关注度最多的10个用户
        List<Long> userIds = followService.highFollowedTop10();
        if(userIds.isEmpty()){
            return List.of();
        }
        List<User> userList =  userMapper.selectListByIds(userIds);
        return userList.stream().map(user -> userToUserVo(user, true,null)).toList();
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
    private UserVo userToUserVo(User user,boolean isFollowCount,String userId) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.setUserId(String.valueOf(user.getUserId()));
        if(isFollowCount){
            userVo.setFansCount(String.valueOf(followService.followNumCount(user.getUserId(), true)));
            userVo.setFollowingCount(String.valueOf(followService.followNumCount(user.getUserId(), false)));
        }
        if(StringUtils.isNotEmpty(userId)){
            FollowStatus userFollowStatus = followService.getUserFollowStatus(userVo.getUserId(), userId);
            if(user.getUserId().toString().equals(userId)){
                userVo.setFollowStatus(FollowStatus.CONFIRMED.getValue());
            }else{
                userVo.setFollowStatus(userFollowStatus.getValue());
            }
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

