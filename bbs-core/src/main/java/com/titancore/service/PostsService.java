package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.PostDTO;
import com.titancore.domain.entity.Posts;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PostFrequencyVo;
import com.titancore.domain.vo.PostVo;

import java.util.List;


public interface PostsService extends IService<Posts> {

    PageResult queryPostList(PostParam postParam);

    PostVo queryPostDetaisById(String postId);

    /**
     * 创建帖子
     * @param postDTO
     * @return
     */
    DMLVo createPost(PostDTO postDTO);

    DMLVo deletePost(String postId);

    /**
     * 获取用户的发帖频率
     * @param userId 用户ID
     * @return 用户发帖频率数据
     */
    List<PostFrequencyVo> getPostFrequency(String userId);
}
