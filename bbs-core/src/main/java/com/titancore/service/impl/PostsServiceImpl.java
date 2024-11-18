package com.titancore.service.impl;

import cn.dev33.satoken.stp.StpUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.lang.reflect.Type;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.PostDTO;
import com.titancore.domain.entity.*;
import com.titancore.domain.mapper.*;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.*;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.RoleType;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.*;
import com.titancore.util.AuthenticationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private TagMapper tagMapper;
    @Autowired
    private MediaUrlService mediaUrlService;
    @Autowired
    private PostContentMapper postContentMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FollowService followService;

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
    @Transactional
    @Override
    public DMLVo createPost(PostDTO postDTO) {
        //当前作者是否与帖子作者一致
        String authorId = postDTO.getAuthorId();
        AuthenticationUtil.checkUserId(authorId);
        //1、帖子主题 从redis获取
        String temporalPostId = stringRedisTemplate.opsForValue().get(RedisConstant.TEMPORARYPOSTID_PRIX + authorId);
        if(StringUtils.isEmpty(temporalPostId)){
            // 生成新的临时文章ID
            SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();
            temporalPostId = String.valueOf(snowflakeGenerator.next());
        }
        //2、帖子内容
        PostContent postContent = new PostContent();
        BeanUtil.copyProperties(postDTO,postContent);
        postContent.setPostId(Long.valueOf(temporalPostId));
        int first = postContentMapper.insert(postContent);

        Posts posts = new Posts();
        BeanUtil.copyProperties(postDTO,posts);
        posts.setId(Long.valueOf(temporalPostId));
        posts.setAuthorId(Long.valueOf(authorId));
        posts.setCategoryId(Long.valueOf(postDTO.getCategoryId()));
        posts.setContentId(postContent.getId());
        int second = postMapper.insert(posts);


        //3、帖子链接保存 异常处理可有可无我认为
        boolean thired = false;
        List<String> mediaUrls = stringRedisTemplate.opsForList().range(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + temporalPostId, 0, -1);
        if(mediaUrls!=null && !mediaUrls.isEmpty()) {
            List<MediaUrl> mediaUrlList = new ArrayList<>();
            for (String mediaUrlJson : mediaUrls) {
                Type type = new TypeReference<MediaUrl>(){}.getType();
                MediaUrl mediaUrl = JSON.parseObject(mediaUrlJson, type);
                mediaUrlList.add(mediaUrl);
            }
            long count = mediaUrlService.queryMediaUrlListByPostId(Long.valueOf(temporalPostId)).size();
            if (mediaUrlList.size() > count){
                mediaUrlService.remove(new LambdaQueryWrapper<MediaUrl>().eq(MediaUrl::getPostId, Long.valueOf(temporalPostId)));
                thired = mediaUrlService.saveBatch(mediaUrlList);
            }
        }
        //4、建立帖子标签
        int fourth = 0;
        if (!postDTO.getTagIds().isEmpty()){
            List<Long> tagIds = postDTO.getTagIds().stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            fourth  = tagMapper.buildRelWithTagIds(Long.valueOf(temporalPostId), tagIds);
        }

        DMLVo dmlVo = new DMLVo();
        if(first > 0 && second > 0){
            dmlVo.setId(temporalPostId);
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_CREATE_SUCCESS);
            //删除redis缓存
            stringRedisTemplate.delete(RedisConstant.TEMPORARYPOSTID_PRIX + authorId);
            stringRedisTemplate.delete(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + temporalPostId);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_CREATE_ERROR);
        }
        return dmlVo;
    }
    @Transactional
    @Override
    public DMLVo deletePost(String postId) {
        //删除帖子标签
        int first = postMapper.deletePostTagRelBypostId(postId);
        //删除帖子链接 todo
        //删除帖子内容
        int third = postContentMapper.delete(new LambdaQueryWrapper<PostContent>().eq(PostContent::getPostId, postId));

        //删除帖子
        int last = postMapper.deleteById(postId);

        DMLVo dmlVo = new DMLVo();
        if(last > 0){
            dmlVo.setId(postId);
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_CREATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_CREATE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public List<PostFrequencyVo> getPostFrequency(String userId) {
        AuthenticationUtil.checkUserId(userId);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(365);
        // 获取发帖数据
        List<PostFrequencyVo> frequencyData  = postMapper.getPostCountByDate(Long.valueOf(userId), startDate.toString(), endDate.toString());
        List<LocalDate> allDates = new ArrayList<>();
        while (!startDate.isAfter(endDate)) {
            allDates.add(LocalDate.from(startDate));
            startDate = startDate.plusDays(1);
        }
        Map<LocalDate, Integer> frequencyMap = frequencyData.stream()
            .collect(Collectors.toMap(
                PostFrequencyVo::getPostDate,
                PostFrequencyVo::getPostCount
            ));

        return allDates.stream()
            .map(date -> {
                Integer postCount = frequencyMap.getOrDefault(date, 0);
                return new PostFrequencyVo(date, postCount);
            })
            .collect(Collectors.toList());
    }
    /**
     * 将帖子对象转换为帖子视图对象
     * @param posts
     * @param isAuthor
     * @param isTag
     * @param isCategory
     * @param isUrl
     * @return
     */
    private PostViewVo PostsToPostViewVo (Posts posts,boolean isAuthor,boolean isTag,boolean isCategory,boolean isUrl){
        PostViewVo postViewVo = new PostViewVo();
        postViewVo.setPostId(posts.getId().toString());
        if(isAuthor){
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, posts.getAuthorId()));
            UserVo userVo = new UserVo();
            userVo.setUserId(String.valueOf(user.getUserId()));
            userVo.setUserName(user.getUserName());
            userVo.setAvatar(user.getAvatar());
            userVo.setBio(user.getBio());
            HashMap<String, Long> map = followService.queryFollowCount(String.valueOf(user.getUserId()));
            userVo.setFollowingCount(map.get("followingCount").toString());
            userVo.setFansCount(map.get("fansCount").toString());
            postViewVo.setUserVo(userVo);
        }
        postViewVo.setTitle(posts.getTitle());
        postViewVo.setSummary(posts.getSummary());
        postViewVo.setCreateTime(posts.getCreateTime());
        postViewVo.setUpdateTime(posts.getUpdateTime());
        postViewVo.setCommentTime(posts.getCommentTime());

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
        postViewVo.setIsTop(posts.getIsTop() != null ? posts.getIsTop().toString() : null);
    return postViewVo;
    }

    /**
     * 将帖子对象转换为帖子视图详细对象
     * @param posts
     * @param isAuthor
     * @param isTag
     * @param isCategory
     * @param isUrl
     * @return
     */
    private PostVo PostsToPostVo (Posts posts,boolean isAuthor,boolean isTag,boolean isCategory,boolean isUrl){
        PostVo postVo = new PostVo();
        postVo.setPostId(posts.getId().toString());
        if(isAuthor){
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, posts.getAuthorId()));
            UserVo userVo = new UserVo();
            userVo.setUserId(String.valueOf(user.getUserId()));
            userVo.setUserName(user.getUserName());
            userVo.setAvatar(user.getAvatar());
            userVo.setBio(user.getBio());
            HashMap<String, Long> map = followService.queryFollowCount(String.valueOf(user.getUserId()));
            userVo.setFollowingCount(map.get("followingCount").toString());
            userVo.setFansCount(map.get("fansCount").toString());
            postVo.setUserVo(userVo);
        }
        postVo.setTitle(posts.getTitle());
        postVo.setSummary(posts.getSummary());
        postVo.setCreateTime(posts.getCreateTime());
        postVo.setUpdateTime(posts.getUpdateTime());
        postVo.setCommentTime(posts.getCommentTime());
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




