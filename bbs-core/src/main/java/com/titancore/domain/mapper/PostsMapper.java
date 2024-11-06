package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.Posts;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostsMapper extends BaseMapper<Posts> {
    /**
     * 根据postId删除帖子标签关联表数据
     * @param postId
     * @return
     */
    @Delete("delete from post_tag_rel where post_id = #{postId}")
    int deletePostTagRelBypostId(String postId);
}




