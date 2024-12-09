package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.PostMediaUrl;
import com.titancore.domain.vo.MediaUrlVo;

import java.util.List;

public interface PostMediaUrlService extends IService<PostMediaUrl> {
    /**
     * 根据PostId查询媒体列表
     * @param id
     * @return
     */
    List<MediaUrlVo> queryMediaUrlListByPostId(Long id);

    /**
     * 根据postId删除媒体文件
     * @param postId
     * @return
     */
    boolean deleteMediaUrlByPostId(String postId);
}
