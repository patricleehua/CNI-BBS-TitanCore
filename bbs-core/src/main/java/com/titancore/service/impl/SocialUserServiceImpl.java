package com.titancore.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.SocialUser;
import com.titancore.domain.entity.User;
import com.titancore.domain.mapper.SocialUserMapper;
import com.titancore.service.SocialUserService;
import jakarta.annotation.Resource;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Service
public class SocialUserServiceImpl extends ServiceImpl<SocialUserMapper, SocialUser> implements SocialUserService {
    @Resource
    private SocialUserMapper socialUserMapper;
    @Override
    public boolean saveSocialUserToDB(AuthUser authUser) {
        if (authUser!=null && !authUser.getUuid().isEmpty()){
            SocialUser socialUser = new SocialUser();
            BeanUtils.copyProperties(authUser,socialUser);
            socialUser.setGender(authUser.getGender().getCode());
            return this.save(socialUser);
        }
        return false;
    }

    @Override
    public SocialUser querySocialUserByUUIDWithSource(String uuid, String source) {
        SocialUser socialUser = null;
        if(uuid!=null && !uuid.isEmpty()){
            socialUser = this.getOne(new LambdaQueryWrapper<SocialUser>().eq(SocialUser::getUuid, uuid).eq(SocialUser::getSource, source).last("limit 1"));
        }
        return socialUser;
    }

    @Override
    public boolean updateSocialUserToDB(AuthUser authUser) {
        SocialUser oldSocialUser = querySocialUserByUUIDWithSource(authUser.getUuid(), authUser.getSource());
        if (oldSocialUser!=null){
            BeanUtils.copyProperties(authUser,oldSocialUser);
            oldSocialUser.setGender(authUser.getGender().getCode());
            return this.updateById(oldSocialUser);
        }
        return false;
    }

    @Override
    public User queryUserFromSocialUserIdBindedSystemUser(String socialUserId) {
        return socialUserMapper.selectUserFromSocialUserRelBySocialUserId(socialUserId);
    }

    @Override
    public User buildThirdUserFromSocialUser(String socialUserId) {
        //todo 异常处理
        SocialUser socialUser = socialUserMapper.selectById(socialUserId);
        User user = new User();
        user.setLoginName(socialUser.getUsername());
        user.setUserName(socialUser.getNickname());
        user.setAvatar(socialUser.getAvatar());
        user.setBio(socialUser.getRemark());
        user.setSex(socialUser.getGender());
        user.setEmail(socialUser.getEmail());

        user.setDelFlag("0");
        user.setCreateBy(socialUser.getUuid());
        user.setUserType("02");
        user.setIsPrivate(1);
        return user;
    }

    @Override
    public int buildRelForSocialUserWithUserByUserIdAndSocialUserId(Long id, Long userId, String socialUserId) {
        return socialUserMapper.buildRelForSocialUserWithUserByUserIdAndSocialUserId(id,userId,socialUserId);
    }

}




