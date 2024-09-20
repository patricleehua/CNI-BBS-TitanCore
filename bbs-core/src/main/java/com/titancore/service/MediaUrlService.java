package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.MediaUrl;
import com.titancore.domain.vo.MediaUrlVo;

import java.util.List;

public interface MediaUrlService extends IService<MediaUrl> {

    List<MediaUrlVo> queryMediaUrlListByPostId(Long id);
}
