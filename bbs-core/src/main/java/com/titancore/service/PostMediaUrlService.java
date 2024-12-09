package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.PostMediaUrl;
import com.titancore.domain.vo.MediaUrlVo;

import java.util.List;

public interface PostMediaUrlService extends IService<PostMediaUrl> {

    List<MediaUrlVo> queryMediaUrlListByPostId(Long id);
}
