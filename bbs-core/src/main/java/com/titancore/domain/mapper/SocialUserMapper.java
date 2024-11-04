package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.SocialUser;
import com.titancore.domain.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface SocialUserMapper extends BaseMapper<SocialUser> {

    @Select("SELECT * FROM user WHERE user_id = (SELECT user_id FROM social_user_rel WHERE social_user_id = #{socialUserId})")
    User selectUserFromSocialUserRelBySocialUserId(@Param("socialUserId") String socialUserId);

    @Insert("INSERT INTO social_user_rel(id, user_id, social_user_id) VALUES (#{id}, #{user_id}, #{social_user_id})")
    int buildRelForSocialUserWithUserByUserIdAndSocialUserId(@Param("id") Long id, @Param("user_id") Long userId, @Param("social_user_id") String socialUserId);

}

