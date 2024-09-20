package com.titancore.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.Tag;
import com.titancore.domain.mapper.TagMapper;
import com.titancore.domain.vo.TagVo;
import com.titancore.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagVo> queryTagListByPostId(Long post_id) {
        //todo redis
        List<Tag> tagList = tagMapper.queryTagListByPostId(post_id);
        List<TagVo> tagVoList = tagList.stream().map(tag -> {
            TagVo tagVo = new TagVo();
            BeanUtils.copyProperties(tag, tagVo);
            return tagVo;
        }).collect(Collectors.toList());
        return tagVoList;
    }
}




