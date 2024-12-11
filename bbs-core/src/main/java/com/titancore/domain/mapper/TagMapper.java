package com.titancore.domain.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据PostId查询标签列表
     * @param post_id
     * @return
     */
    List<Tag> queryTagListByPostId(Long post_id);

    /**
     * 根据PostId和TagId列表建立关系
     * @param postId
     * @param tagIds
     * @return
     */
    int buildRelWithTagIds(@Param("postId") Long postId, @Param("tagIds") List<Long> tagIds);

    /**
     * 根据PostId删除与Tag的关联
     * @param postId
     * @return
     */
    int deleteRelByPostId(@Param("postId") Long postId);
}




