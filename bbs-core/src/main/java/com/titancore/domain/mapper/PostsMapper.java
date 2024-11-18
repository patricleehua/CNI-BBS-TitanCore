package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.Posts;
import com.titancore.domain.vo.PostFrequencyVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostsMapper extends BaseMapper<Posts> {
    /**
     * 根据postId删除帖子标签关联表数据
     * @param postId
     * @return
     */
    @Delete("delete from post_tag_rel where post_id = #{postId}")
    int deletePostTagRelBypostId(String postId);

    /**
     * 查询用户发帖频率
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
     @Select("SELECT " +
            "DATE(create_time)AS post_date, " +
            "COUNT(*) AS post_count " +
            "FROM posts " +
            "WHERE author_id = #{userId} " +
            "AND create_time >= #{startDate} " +
            "AND create_time <= #{endDate} " +
            "GROUP BY post_date " +
            "ORDER BY post_date ASC")
     List<PostFrequencyVo> getPostCountByDate(@Param("userId")Long userId, @Param("startDate") String startDate,@Param("endDate") String endDate);
}




