package com.titancore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.lang.reflect.Type;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.CleanCoverDTO;
import com.titancore.domain.dto.PostDTO;
import com.titancore.domain.dto.PostUpdateDTO;
import com.titancore.domain.entity.*;
import com.titancore.domain.mapper.*;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostParam;
import com.titancore.domain.vo.*;
import com.titancore.enums.FollowStatus;
import com.titancore.enums.LinkType;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.*;
import com.titancore.util.AuthenticationUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements PostsService {
    private final PostsMapper postMapper;
    private final UserMapper userMapper;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final TagMapper tagMapper;
    private final PostMediaUrlService postMediaUrlService;
    private final PostContentMapper postContentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final FollowService followService;
    private final CommonService commonService;
    private final ElasticSearch8Service elasticSearch16Service;

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
                .map(posts-> PostsToPostViewVo(posts,true,true,true,true,postParam.getUserId())).collect(Collectors.toList());
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
        //1、帖子ID 从redis获取
        String temporalPostId = stringRedisTemplate.opsForValue().get(RedisConstant.TEMPORARYPOSTID_PRIX + authorId);
        if(StringUtils.isEmpty(temporalPostId)){
            throw new BizException(ResponseCodeEnum.TEMPORARY_POST_ID_NOT_EXISTS);
//            // 生成新的临时文章ID
//            SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();
//            temporalPostId = String.valueOf(snowflakeGenerator.next());
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
            List<PostMediaUrl> postMediaUrlList = new ArrayList<>();
            for (String mediaUrlJson : mediaUrls) {
                Type type = new TypeReference<PostMediaUrl>(){}.getType();
                PostMediaUrl postMediaUrl = JSON.parseObject(mediaUrlJson, type);
                postMediaUrlList.add(postMediaUrl);
            }
            long count = postMediaUrlService.queryMediaUrlListByPostId(Long.valueOf(temporalPostId)).size();
            if (postMediaUrlList.size() > count){
                postMediaUrlService.remove(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, Long.valueOf(temporalPostId)));
                thired = postMediaUrlService.saveBatch(postMediaUrlList);
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
            elasticSearch16Service.insertPostDoc("cni-post",temporalPostId);
            //删除redis缓存
            removeTemporaryPostId(authorId);
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

        //删除帖子链接
        boolean second = postMediaUrlService.deleteMediaUrlByPostId(postId);

        //删除帖子内容
        int third = postContentMapper.delete(new LambdaQueryWrapper<PostContent>().eq(PostContent::getPostId, postId));

        //删除帖子
        int last = postMapper.deleteById(postId);

        DMLVo dmlVo = new DMLVo();
        if(last > 0){
            dmlVo.setId(postId);
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_DELETE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_DELETE_ERROR);
        }
        return dmlVo;
    }


    @Override
    public String createTemporaryPostId(String userId,String postId) {
        String existingPostId = stringRedisTemplate.opsForValue().get(RedisConstant.TEMPORARYPOSTID_PRIX + userId);
        if (existingPostId != null) {
            stringRedisTemplate.expire(RedisConstant.TEMPORARYPOSTID_PRIX + userId, RedisConstant.TEMPORARYPOSTID_TTL, TimeUnit.DAYS);
            return existingPostId;
        }
        if (postId == null || postId.isEmpty()){
            SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();
            String newPostId = String.valueOf(snowflakeGenerator.next());
            stringRedisTemplate.opsForValue().set(RedisConstant.TEMPORARYPOSTID_PRIX + userId, newPostId, RedisConstant.TEMPORARYPOSTID_TTL, TimeUnit.DAYS);
            return newPostId;
        }
        stringRedisTemplate.opsForValue().set(RedisConstant.TEMPORARYPOSTID_PRIX + userId, postId, RedisConstant.TEMPORARYPOSTID_TTL, TimeUnit.DAYS);
        return postId;
    }

    @Override
    public DMLVo cleanTemporaryCover(CleanCoverDTO cleanCoverDTO) {
        String userId = cleanCoverDTO.getAuthorId();
        AuthenticationUtil.checkUserId(userId);
        String key = RedisConstant.TEMPORARYPOSTMEDIA_PRIX + cleanCoverDTO.getPostId();
        List<String> mediaUrls = stringRedisTemplate.opsForList().range(key, 0, -1);
        if(mediaUrls!=null && !mediaUrls.isEmpty()) {
            List<PostMediaUrl> postMediaUrlList = new ArrayList<>();
            for (String mediaUrlJson : mediaUrls) {
                Type type = new TypeReference<PostMediaUrl>(){}.getType();
                PostMediaUrl postMediaUrl = JSON.parseObject(mediaUrlJson, type);
                postMediaUrlList.add(postMediaUrl);
            }
            List<PostMediaUrl> newLiit = postMediaUrlList.stream().filter(postMediaUrl -> !postMediaUrl.getMediaUrl().equals(cleanCoverDTO.getCoverUrl())).toList();
            Long size = stringRedisTemplate.opsForList().size(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + cleanCoverDTO.getPostId());
            stringRedisTemplate.delete(key);
            for (PostMediaUrl postMediaUrl : newLiit) {
                if (size == null || size == 0) {
                    stringRedisTemplate.opsForList().rightPush(RedisConstant.TEMPORARYPOSTMEDIA_PRIX +  cleanCoverDTO.getPostId(), JSON.toJSONString(postMediaUrl));
                    stringRedisTemplate.expire(RedisConstant.TEMPORARYPOSTMEDIA_PRIX +  cleanCoverDTO.getPostId(), RedisConstant.TEMPORARYPOSTMEDIA_TTL, TimeUnit.HOURS);
                } else {
                    stringRedisTemplate.opsForList().rightPush(RedisConstant.TEMPORARYPOSTMEDIA_PRIX +  cleanCoverDTO.getPostId(), JSON.toJSONString(postMediaUrl));
                }
            }
        }
        postMediaUrlService.remove(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getMediaUrl, cleanCoverDTO.getCoverUrl()));

        DMLVo dmlVo = new DMLVo();
        dmlVo.setId(cleanCoverDTO.getPostId());
        dmlVo.setStatus(true);
        dmlVo.setMessage(CommonConstant.DML_DELETE_SUCCESS);
        return dmlVo;
    }

    private String checkTemporaryPostIdIsExist(String userId) {
        String existingPostId = stringRedisTemplate.opsForValue().get(RedisConstant.TEMPORARYPOSTID_PRIX + userId);
        if (existingPostId == null){
            throw new BizException(ResponseCodeEnum.TEMPORARY_POST_ID_NOT_EXISTS);
        }
        return existingPostId;
    }
    public boolean removeTemporaryPostId(String userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(RedisConstant.TEMPORARYPOSTID_PRIX + userId));
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

    @Override
    public PostUpdateInfoVo getUpdatePostInfo(String postId,String userId) {
        AuthenticationUtil.checkUserId(userId);
        String temporalPostId = stringRedisTemplate.opsForValue().get(RedisConstant.TEMPORARYPOSTID_PRIX + userId);
        if(StringUtils.isEmpty(temporalPostId)){
            throw new BizException(ResponseCodeEnum.TEMPORARY_POST_ID_NOT_EXISTS);
        }
        //1、获取帖子对象
        Posts posts = postMapper.selectById(postId);
        if (posts == null){
            throw new BizException(ResponseCodeEnum.POST_POST_IS_NOT_EXIST);
        }else if(!postId.equals(temporalPostId)){
            throw new BizException(ResponseCodeEnum.POST_POST_ID_NOT_CORRECT);
        }
        PostUpdateInfoVo postUpdateInfoVo = new PostUpdateInfoVo();
        postUpdateInfoVo.setPostId(postId);
        postUpdateInfoVo.setUserId(String.valueOf(posts.getAuthorId()));
        postUpdateInfoVo.setTitle(posts.getTitle());
        postUpdateInfoVo.setSummary(posts.getSummary());

        PostContent postContent = postContentMapper.selectById(posts.getContentId());
        HashMap<String,String> posthashmap =  new HashMap<>();
        posthashmap.put("content",postContent.getContent());
        posthashmap.put("contentHtml",postContent.getContentHtml());
        postUpdateInfoVo.setPostContent(posthashmap);

        postUpdateInfoVo.setCategoryId(String.valueOf(posts.getCategoryId()));

        List<String> tags = tagMapper.queryTagListByPostId(posts.getId()).stream().map(tag -> String.valueOf(tag.getId())).toList();
        postUpdateInfoVo.setTagIds(tags);

        List<PostMediaUrlVo> postMediaUrlVos = postMediaUrlService.queryMediaUrlListByPostId(posts.getId());
        postUpdateInfoVo.setUrls(postMediaUrlVos);

        postUpdateInfoVo.setType(String.valueOf(posts.getType()));
        return postUpdateInfoVo;
    }

    @Transactional
    @Override
    public DMLVo updatePost(PostUpdateDTO postUpdateDTO) {
        String authorId = postUpdateDTO.getAuthorId();
        AuthenticationUtil.checkUserId(authorId);
        // 获取分布式锁
        String lockKey = RedisConstant.POST_UPDATE_LOCK_PRIX + postUpdateDTO.getPostId();
        String lockValue = UUID.randomUUID().toString();
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 1, TimeUnit.MINUTES))) {
                String temporaryPostId = checkTemporaryPostIdIsExist(authorId);
                if (!temporaryPostId.equals(postUpdateDTO.getPostId())) {
                    throw new BizException(ResponseCodeEnum.POST_POST_ID_NOT_CORRECT);
                }
                //修改帖子信息
                LambdaUpdateWrapper<Posts> postsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                postsLambdaUpdateWrapper.set(Posts::getTitle, postUpdateDTO.getTitle());
                postsLambdaUpdateWrapper.set(Posts::getSummary, postUpdateDTO.getSummary());
                postsLambdaUpdateWrapper.set(Posts::getCategoryId, postUpdateDTO.getCategoryId());
                postsLambdaUpdateWrapper.set(Posts::getType, postUpdateDTO.getType());
                postsLambdaUpdateWrapper.eq(Posts::getId, postUpdateDTO.getPostId());
                int first = postMapper.update(postsLambdaUpdateWrapper);
                //修改帖子内容
                LambdaUpdateWrapper<PostContent> postContentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                postContentLambdaUpdateWrapper.set(PostContent::getContent, postUpdateDTO.getContent());
                postContentLambdaUpdateWrapper.set(PostContent::getContentHtml, postUpdateDTO.getContentHtml());
                postContentLambdaUpdateWrapper.eq(PostContent::getPostId, postUpdateDTO.getPostId());
                int second = postContentMapper.update(postContentLambdaUpdateWrapper);
                //删除旧标签
                int third = tagMapper.deleteRelByPostId(Long.valueOf(postUpdateDTO.getPostId()));
                //插入新标签
                List<Long> tagIds = postUpdateDTO.getTagIds().stream().map(Long::valueOf).toList();
                int fourth = tagMapper.buildRelWithTagIds(Long.valueOf(postUpdateDTO.getPostId()), tagIds);

                // 更新媒体库业务逻辑
                //获取数据库中的媒体列表
                List<PostMediaUrlVo> rawPostMediaUrlVos = postMediaUrlService.queryMediaUrlListByPostId(Long.valueOf(postUpdateDTO.getPostId()));

                // 从 Redis 获取媒体 URL 列表
                List<String> mediaUrls = stringRedisTemplate.opsForList().range(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + postUpdateDTO.getPostId(), 0, -1);
                if (mediaUrls != null && !mediaUrls.isEmpty()) {
                    List<PostMediaUrl> redisPostMediaUrlList = mediaUrls.stream()
                            .map(mediaUrlJson -> JSON.parseObject(mediaUrlJson, PostMediaUrl.class))
                            .toList();

                    // 获取用户上传的新媒体 URL 列表
                    List<PostMediaUrlVo> userUploadedMediaUrls = postUpdateDTO.getPostMediaUrls();
                    HashSet<String> userUploadedSet = userUploadedMediaUrls.stream()
                            .map(PostMediaUrlVo::getMediaUrl)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 把不在用户上传列表中的 Redis 的URL删除
                    for (PostMediaUrl redisPostMediaUrl : redisPostMediaUrlList) {
                        String url = redisPostMediaUrl.getMediaUrl();
                        if (!userUploadedSet.contains(url)) {
                            FileDelDTO fileDelDTO = commonService.urlToFileDelDTO(url);
                            commonService.deleteFile(fileDelDTO);
                            postMediaUrlService.deleteMediaUrlById(redisPostMediaUrl.getId());
                        }
                    }

                    // 对用户上传的 URL 进行检查
                    Set<String> rawMediaUrlsForCoverSet = rawPostMediaUrlVos.stream()
                            .filter(postMediaUrlVo -> LinkType.COVER.getValue().equals(postMediaUrlVo.getMediaType()))
                            .sorted(Comparator.comparing(PostMediaUrlVo::getCreateTime))
                            .map(PostMediaUrlVo::getMediaUrl)
                            .collect(Collectors.toSet());

                    String content = postUpdateDTO.getContent();
                    for (PostMediaUrlVo userUploadedUrl : userUploadedMediaUrls) {
                        String uploadedUrl = userUploadedUrl.getMediaUrl();
                        LinkType linkType = LinkType.valueOfAll(userUploadedUrl.getMediaType());

                        if (linkType != null) {
                            switch (linkType) {
                                case BACKGROUND,VIDEO -> {
                                    if (!content.contains(uploadedUrl)) {
                                        // 如果 content 中没有包含该 URL，则删除它
                                        FileDelDTO fileDelDTO = commonService.urlToFileDelDTO(uploadedUrl);
                                        commonService.deleteFile(fileDelDTO);
                                        postMediaUrlService.deleteMediaUrlByUrl(uploadedUrl);
                                    }
                                }
                                case COVER -> {
                                    if (!rawMediaUrlsForCoverSet.contains(uploadedUrl)) {
                                        // 如果用户上传的 cover URL 在原始媒体库中不存在，则删除它
                                        FileDelDTO fileDelDTO = commonService.urlToFileDelDTO(uploadedUrl);
                                        commonService.deleteFile(fileDelDTO);
                                        postMediaUrlService.deleteMediaUrlByUrl(uploadedUrl);
                                    }
                                }
                            }
                        }
                    }
                    for (PostMediaUrlVo rawPostMediaUrlVo : rawPostMediaUrlVos) {
                        String rawUrl = rawPostMediaUrlVo.getMediaUrl();
                        if (!userUploadedSet.contains(rawUrl)) {
                            FileDelDTO fileDelDTO = commonService.urlToFileDelDTO(rawUrl);
                            commonService.deleteFile(fileDelDTO);
                            postMediaUrlService.deleteMediaUrlByUrl(rawUrl);
                        }
                    }
                }
                DMLVo dmlVo = new DMLVo();
                dmlVo.setId(postUpdateDTO.getPostId());
                if (first*second*third*fourth>0){
                    dmlVo.setMessage(CommonConstant.DML_UPDATE_SUCCESS);
                    dmlVo.setStatus(true);
                    //删除redis缓存
                    removeTemporaryPostId(authorId);
                }else {
                    dmlVo.setMessage(CommonConstant.DML_UPDATE_ERROR);
                    dmlVo.setStatus(false);
                }
                return dmlVo;
            }
            else {
                Thread.sleep(100);
            }
        }catch (InterruptedException e){
            throw new BizException(ResponseCodeEnum.REDIS_LOCK_KEY_FREE_ERROR);
        }finally {
            try{
                String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
                if (!StringUtils.isEmpty(currentValue) && currentValue.equals(lockValue)) {
                    stringRedisTemplate.delete(lockKey);
                }
            }catch (Exception e){
                throw new BizException(ResponseCodeEnum.REDIS_LOCK_KEY_FREE_ERROR);
            }
        }
        throw new BizException(ResponseCodeEnum.POST_UPDATE_ERROR);
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
    private PostViewVo PostsToPostViewVo (Posts posts,boolean isAuthor,boolean isTag,boolean isCategory,boolean isUrl,String userId){
        PostViewVo postViewVo = new PostViewVo();
        postViewVo.setPostId(posts.getId().toString());
        if(isAuthor){
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, posts.getAuthorId()));
            UserVo userVo = new UserVo();
            if (StringUtils.isNotBlank(userId)) {
                FollowStatus userFollowStatus = followService.getUserFollowStatus(String.valueOf(user.getUserId()), userId);
                if(user.getUserId().toString().equals(userId)){
                    userVo.setFollowStatus(FollowStatus.CONFIRMED.getValue());
                }else{
                    userVo.setFollowStatus(userFollowStatus.getValue());
                }
            }
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
            List<PostMediaUrlVo> postMediaUrlVos = postMediaUrlService.queryMediaUrlListByPostId(posts.getId());
            postViewVo.setUrls(postMediaUrlVos);
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
            List<PostMediaUrlVo> postMediaUrlVos = postMediaUrlService.queryMediaUrlListByPostId(posts.getId());
            postVo.setUrls(postMediaUrlVos);
        }
        postVo.setType(posts.getType() != null ? posts.getType().toString() : null);
        postVo.setView_counts(posts.getViewCounts() != null ? posts.getViewCounts().toString() : null);
        return postVo;
    }
}




