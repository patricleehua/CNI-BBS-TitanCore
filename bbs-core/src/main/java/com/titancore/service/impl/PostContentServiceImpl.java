package com.titancore.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.PostContent;
import com.titancore.domain.mapper.PostContentMapper;
import com.titancore.service.PostContentService;
import org.springframework.stereotype.Service;

@Service
public class PostContentServiceImpl extends ServiceImpl<PostContentMapper, PostContent> implements PostContentService {

}




