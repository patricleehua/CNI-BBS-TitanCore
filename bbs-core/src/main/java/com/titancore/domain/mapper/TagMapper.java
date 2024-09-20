package com.titancore.domain.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> queryTagListByPostId(Long post_id);
}




