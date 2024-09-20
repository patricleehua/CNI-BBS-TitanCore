package com.titancore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.*;
import com.titancore.domain.mapper.CategoryMapper;
import com.titancore.domain.mapper.PostContentMapper;
import com.titancore.domain.mapper.PostsMapper;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.*;
import com.titancore.service.CategoryService;
import com.titancore.service.MediaUrlService;
import com.titancore.service.PostsService;
import com.titancore.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements PostsService {
    @Autowired
    private PostsMapper postMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private MediaUrlService mediaUrlService;
    @Autowired
    private PostContentMapper postContentMapper;
    @Override
    public PageResult queryPostList(PostParam postParam) {
        Page<Posts> page = new Page<>(postParam.getPageNo(), postParam.getPageSize());
        LambdaQueryWrapper<Posts> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(postParam.getPostId())) {
            queryWrapper.eq(Posts::getId, postParam.getPostId());
        }

        if (StringUtils.isNotBlank(postParam.getCategoryId())) {
            queryWrapper.eq(Posts::getCategoryId, postParam.getCategoryId());
        }

        Page<Posts> postsPage = postMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(postsPage.getTotal());
        //过滤
        List<PostViewVo> postViewVos = postsPage.getRecords().stream()
                .map(posts-> PostsToPostViewVo(posts,true,true,true,true)).collect(Collectors.toList());
        pageResult.setRecords(postViewVos);
        return pageResult;
    }

    @Override
    public PostVo queryPostDetaisById(String postId) {
        Posts posts = postMapper.selectById(postId);
        PostVo postVo = PostsToPostVo(posts, true, true, true, true);
        return postVo;
    }


    private PostViewVo PostsToPostViewVo (Posts posts,boolean isAuthor,boolean isTag,boolean isCategory,boolean isUrl){
        PostViewVo postViewVo = new PostViewVo();
        postViewVo.setPostId(posts.getId().toString());
        if(isAuthor){
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, posts.getAuthorId()));
            postViewVo.setAuthorName(user.getUserName());
        }
        postViewVo.setTitle(posts.getTitle());
        postViewVo.setSummary(posts.getSummary());
        postViewVo.setCreate_date(posts.getCreateDate());
        postViewVo.setUpdate_date(posts.getUpdateDate());
        postViewVo.setComment_date(posts.getCommentDate());

        if(isCategory){
            CategoryVo categoryVo = categoryService.queryCategoryById(posts.getCategoryId());
            postViewVo.setCategoryVo(categoryVo);
        }
        if(isTag){
            List<TagVo> tags = tagService.queryTagListByPostId(posts.getId());
            postViewVo.setTagVos(tags);
        }
        if(isUrl){
            List<MediaUrlVo> mediaUrlVos = mediaUrlService.queryMediaUrlListByPostId(posts.getId());
            postViewVo.setUrls(mediaUrlVos);
        }
        postViewVo.setType(posts.getType() != null ? posts.getType().toString() : null);
        postViewVo.setView_counts(posts.getViewCounts() != null ? posts.getViewCounts().toString() : null);
        postViewVo.setIstop(posts.getIstop() != null ? posts.getIstop().toString() : null);
    return postViewVo;
    }

    private PostVo PostsToPostVo (Posts posts,boolean isAuthor,boolean isTag,boolean isCategory,boolean isUrl){
        PostVo postVo = new PostVo();
        postVo.setPostId(posts.getId().toString());
        if(isAuthor){
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, posts.getAuthorId()));
            postVo.setAuthorName(user.getUserName());
        }
        postVo.setTitle(posts.getTitle());
        postVo.setSummary(posts.getSummary());
        postVo.setCreate_date(posts.getCreateDate());
        postVo.setUpdate_date(posts.getUpdateDate());
        postVo.setComment_date(posts.getCommentDate());
        PostContent postContent = postContentMapper.selectOne(new LambdaQueryWrapper<PostContent>().eq(PostContent::getId, posts.getContentId()));
        HashMap<String,String> posthashmap =  new HashMap<>();
        posthashmap.put("content",postContent.getContent());
        posthashmap.put("contentHtml",postContent.getContentHtml());
        postVo.setPostContent(posthashmap);
        if(isCategory){
            CategoryVo categoryVo = categoryService.queryCategoryById(posts.getCategoryId());
            postVo.setCategoryVo(categoryVo);
        }
        if(isTag){
            List<TagVo> tags = tagService.queryTagListByPostId(posts.getId());
            postVo.setTagVos(tags);
        }
        if(isUrl){
            List<MediaUrlVo> mediaUrlVos = mediaUrlService.queryMediaUrlListByPostId(posts.getId());
            postVo.setUrls(mediaUrlVos);
        }
        postVo.setType(posts.getType() != null ? posts.getType().toString() : null);
        postVo.setView_counts(posts.getViewCounts() != null ? posts.getViewCounts().toString() : null);
        return postVo;
    }
}




