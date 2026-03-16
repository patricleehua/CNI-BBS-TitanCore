package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.PostComments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author leehua
* @description 针对表【post_comments(帖子评论表)】的数据库操作Mapper
* @createDate 2024-12-12 13:07:11
* @Entity generator.domain.PostComments
*/
@Mapper
public interface PostCommentsMapper extends BaseMapper<PostComments> {

    /**
     * 查询用户的评论数量
     * @param userId
     * @return
     */
    @Select("SELECT COUNT(*) FROM post_comments WHERE user_id = #{userId}")
    Long selectCountByUserId(@Param("userId") Long userId);
}




