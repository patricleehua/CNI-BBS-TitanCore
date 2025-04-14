package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.SocialUser;
import com.titancore.domain.entity.User;
import me.zhyd.oauth.model.AuthUser;

public interface SocialUserService extends IService<SocialUser> {

    /**
     * 保存第三方用户信息
     * @param authUser
     * @return
     */
    boolean saveSocialUserToDB(AuthUser authUser);

    /**
     * 根据第三方uuid和来源查询第三方用户信息
     * @param uuid
     * @param source
     * @return
     */
    SocialUser querySocialUserByUUIDWithSource(String uuid, String source);

    /**
     * 更新第三方用户信息
     * @param authUser
     * @return
     */
    boolean updateSocialUserToDB(AuthUser authUser);

    /**
     * 根据第三方用户id查询系统用户信息
     * @param socialUserId
     * @return
     */
    User queryUserFromSocialUserIdBindedSystemUser(String socialUserId);

    /**
     * 根据第三方用户信息构建第三方平台注册用户
     * @param socialUserId
     * @return
     */
    User buildThirdUserFromSocialUser(String socialUserId);

    /**
     * 通过用户 ID 和第三方平台用户 ID 为社交用户构建 关系
     * @param id
     * @param userId
     * @param socialUserId
     * @return
     */
    int buildRelForSocialUserWithUserByUserIdAndSocialUserId(Long id, Long userId, String socialUserId);

    int countBindUser(String socialUserId);
}
