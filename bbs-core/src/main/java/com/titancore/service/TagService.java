package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.Tag;
import com.titancore.domain.vo.TagVo;

import java.util.List;

public interface TagService extends IService<Tag> {

    List<TagVo> queryTagListByPostId(Long id);
}
