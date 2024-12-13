package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.PostCommentsDTO;
import com.titancore.domain.entity.PostComments;
import com.titancore.domain.entity.Posts;
import com.titancore.domain.mapper.PostCommentsMapper;
import com.titancore.domain.mapper.PostsMapper;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostCommentParam;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.PostCommentVo;
import com.titancore.domain.vo.UserVo;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.PostCommentsService;
import com.titancore.service.PostsService;
import com.titancore.service.UserService;
import com.titancore.util.AuthenticationUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author leehua
* @description 针对表【post_comments(帖子评论表)】的数据库操作Service实现
* @createDate 2024-12-12 13:07:11
*/
@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
    implements PostCommentsService {
    @Resource
    private UserService userService;
    @Resource
    private PostsMapper postsMapper;
    @Override
    public PageResult queryCommentListByPostId(PostCommentParam postCommentParam) {
        Page<PostComments> page = new Page<>(postCommentParam.getPageNo(), postCommentParam.getPageSize());
        Page<PostComments> postsCommentPage = this.baseMapper.selectPage(page,
                new LambdaQueryWrapper<PostComments>().eq(PostComments::getPostId, postCommentParam.getPostId()).eq(PostComments::getLevel, 1));
        List<PostComments> records = postsCommentPage.getRecords();
        List<PostCommentVo> postCommentVos = postCommentsListToPostCommentVoList(records);
        return new PageResult(postsCommentPage.getTotal(), postCommentVos);
    }

    @Override
    public Map<String, String> addPostComment(PostCommentsDTO postCommentsDTO) {
        String userId = postCommentsDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        String postCommentContent = postCommentsDTO.getContent();
        //todo 敏感词过滤
        boolean isPassedFilter = false;
        Map<String,String> map = new HashMap<>();
//        if (!isPassedFilter){
//            map.put("badWord","reason");
//            return map;
//        }
        Posts posts = postsMapper.selectOne(new LambdaQueryWrapper<Posts>().eq(Posts::getId, postCommentsDTO.getPostId()));
        PostComments postComments = new PostComments();
        postComments.setUserId(Long.valueOf(userId));
        postComments.setPostId(Long.valueOf(postCommentsDTO.getPostId()));
        postComments.setContent(postCommentsDTO.getContent());
        // 如果该评论没有父评论则将该评论设为父评论，否则为子评论
        if (Long.parseLong(postCommentsDTO.getParentId()) == 0
                && Long.parseLong(postCommentsDTO.getReplyCommentId()) == 0
                && Long.parseLong(postCommentsDTO.getToUserId()) == posts.getAuthorId()) {
            //设置为父评论
            postComments.setLevel(1);
            postComments.setParentId(0L);
            postComments.setToUid(posts.getAuthorId());
            postComments.setReplyCommentId(0L);
        } else {
            //设置为子评论
            postComments.setLevel(2);
            postComments.setParentId(Long.valueOf(postCommentsDTO.getParentId()));
            postComments.setReplyCommentId(Long.valueOf(postCommentsDTO.getReplyCommentId()));
            postComments.setToUid(Long.valueOf(postCommentsDTO.getToUserId()));
        }
        //插入评论
        this.save(postComments);
        //更新帖子评论时间 todo 定时任务类/异步调用处理
        //threadService.updateCommentCountAndCommentDate(posts);
        map.put("commentId",postComments.getId().toString());
        //todo更新缓存
        return map;
    }

    @Override
    public DMLVo deletePostComment(PostCommentParam postCommentParam) {
        String userId = postCommentParam.getUserId();
        AuthenticationUtil.checkUserId(userId);
        String postCommentId = postCommentParam.getPostCommentId();
        if (postCommentId == null ){
            throw new BizException(ResponseCodeEnum.POST_COMMENT_COMMENTID_CAN_NOT_BE_NULL);
        }
        boolean isPostsAuthor = postsMapper.exists(new LambdaQueryWrapper<Posts>().eq(Posts::getId, postCommentParam.getPostId()).eq(Posts::getAuthorId, userId));
        boolean isCommentAuthor = this.baseMapper.exists(new LambdaQueryWrapper<PostComments>().eq(PostComments::getId, postCommentId).eq(PostComments::getUserId, userId).last("limit 1"));
        boolean isSuperPowerUser = AuthenticationUtil.hasSuperPowerUser(userId);
        DMLVo dmlVo = new DMLVo();
        dmlVo.setId(postCommentId);
        // 检查用户权限
        if (isPostsAuthor || isCommentAuthor || isSuperPowerUser) {
            if (this.baseMapper.deleteById(postCommentId) > 0) {
                dmlVo.setStatus(true);
                dmlVo.setMessage(CommonConstant.DML_DELETE_SUCCESS);
            } else {
                dmlVo.setStatus(false);
                dmlVo.setMessage(CommonConstant.DML_DELETE_ERROR);
            }
        } else {
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_DELETE_ERROR);
        }
        return dmlVo;
    }

    private List<PostCommentVo> postCommentsListToPostCommentVoList(List<PostComments> postCommentsList) {
        List<PostCommentVo> commentVoList = new ArrayList<>();
        for (PostComments postComments : postCommentsList){
            commentVoList.add(postCommentToCommentVo(postComments));
        }
        return commentVoList;
    }
    private PostCommentVo postCommentToCommentVo(PostComments postComments) {
        PostCommentVo postCommentVo = new PostCommentVo();
        BeanUtils.copyProperties(postComments,postCommentVo);
        postCommentVo.setId(String.valueOf(postComments.getId()));
        //评论者信息
        Long userId = postComments.getUserId();
        UserVo userVo = userService.findUserInfoByUserId(userId);
        postCommentVo.setUser(userVo);

        //获取评论类型
        Integer level = postComments.getLevel();
        if (1 == level){
            Long id = postComments.getId();
            List<PostCommentVo> commentVoList = findCommentsByParentId(id);
            postCommentVo.setChildrens(commentVoList);
        }
        //to User 给谁回复
        if (level > 1){
            Long toUid = postComments.getToUid();
            UserVo toUserVo = userService.findUserInfoByUserId(toUid);
            postCommentVo.setToUser(toUserVo);
        }
        return postCommentVo;
    }
    private List<PostCommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<PostComments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostComments::getParentId,id);
        queryWrapper.eq(PostComments::getLevel,2);
        return postCommentsListToPostCommentVoList(this.baseMapper.selectList(queryWrapper));
    }
}




