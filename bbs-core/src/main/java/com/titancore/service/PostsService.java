package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.entity.Posts;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.PostVo;

import java.util.List;

public interface PostsService extends IService<Posts> {

    PageResult queryPostList(PostParam postParam);

    PostVo queryPostDetaisById(String postId);
}
