package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.FollowDTO;
import com.titancore.domain.entity.Follow;
import com.titancore.domain.entity.User;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.param.FollowParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.DMLVo;
import com.titancore.domain.vo.FollowerVo;
import com.titancore.enums.FollowStatus;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.StatusEnum;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.FollowService;
import com.titancore.domain.mapper.FollowMapper;
import com.titancore.util.AuthenticationUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//todo 异常处理
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{
    @Resource
    private FollowMapper followMapper;
    @Resource
    private UserMapper userMapper;
    @Override
    public PageResult queryList(FollowParam followParam) {
        Page<Follow> page = new Page<>(followParam.getPageNo(), followParam.getPageSize());
        LambdaQueryWrapper<Follow> queryWrapper =  new LambdaQueryWrapper<>();
        boolean isCheckFollower = followParam.isCheckFollower();
        if(isCheckFollower){
            queryWrapper.eq(Follow::getUserId, followParam.getUserId());
        }else{
            queryWrapper.eq(Follow::getFollowerId, followParam.getUserId());
        }
        queryWrapper.eq(Follow::getFollowStatus,FollowStatus.CONFIRMED);
        // todo 待处理细节
//        queryWrapper
//                .like(Follow::getRemark,followParam.getFriendsInfo())
//                .or()
//                .like(Follow::getGroupId,followParam.getFriendsInfo());
        Page<Follow> followPage = followMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(followPage.getTotal());
        List<FollowerVo> records =filterFollowListToFollowerVoList(followPage.getRecords(),false,isCheckFollower);
        pageResult.setRecords(records);
        return pageResult;
    }

    @Override
    public DMLVo buildFollow(FollowDTO followDTO) {

        //当前关注者的信息是否与当前用户一致
        String userId = followDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        //查询原始数据
        Follow followOrg = followMapper.selectOne(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowerId, followDTO.getUserId()).eq(Follow::getUserId, followDTO.getFollowerId()).last("limit 1"));
        if(followOrg != null && followOrg.getFollowStatus().getValue() != null){
            throw new BizException(ResponseCodeEnum.FOLLOW_STRUCTURE_IS_EXIST);
        }
        //检查被关注用户是否为公开账户
        User follower = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, followDTO.getFollowerId()));

        Follow follow = new Follow();
        follow.setUserId(Long.valueOf(followDTO.getFollowerId()));
        follow.setFollowerId(Long.valueOf(userId));
        if(follower.getIsPrivate() == StatusEnum.DISABLED.getValue()){
            follow.setFollowStatus(FollowStatus.CONFIRMED);
        }else{
            follow.setFollowStatus(FollowStatus.PENDING);
        }
        follow.setRemark(follow.getRemark());
        follow.setGroupId(Long.valueOf(followDTO.getGroupId()));
        int result = followMapper.insert(follow);
        // todo 消息发送到消息队列 -> websocket推送给被关注用户
        DMLVo dmlVo = new DMLVo();
        if (result > 0 ){
            dmlVo.setId(String.valueOf(follow.getId()));
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_CREATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_CREATE_ERROR);
        }
        return dmlVo;
    }

    @Transactional
    @Override
    public DMLVo changeFollowStatus(FollowDTO followDTO) {
        //当前用户的信息是否与当前用户一致
        String userId = followDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        boolean isAcceptRequested = followDTO.isAcceptRequested();
        //查询原始数据
        Follow follow = followMapper.selectOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, followDTO.getUserId()).eq(Follow::getFollowerId, followDTO.getFollowerId()).last("limit 1"));
        if(follow == null){
            throw new BizException(ResponseCodeEnum.FOLLOW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<Follow> updateWrapper = new LambdaUpdateWrapper<>();
        if(isAcceptRequested){
            updateWrapper.set(Follow::getFollowStatus,FollowStatus.CONFIRMED.getValue());
            //为对方建立关系 todo 是否需要？
            Follow follower = new Follow();
            follower.setUserId(Long.valueOf(followDTO.getFollowerId()));
            follower.setFollowerId(Long.valueOf(userId));
            follower.setFollowStatus(FollowStatus.CONFIRMED);
            followMapper.insert(follower);
        }else{
            updateWrapper.set(Follow::getFollowStatus,FollowStatus.REJECTED.getValue());
        }
        updateWrapper.eq(Follow::getUserId,followDTO.getUserId());
        updateWrapper.eq(Follow::getFollowerId,followDTO.getFollowerId());
        int result = followMapper.update(updateWrapper);
        DMLVo dmlVo = new DMLVo();
        if (result > 0 ){
            dmlVo.setId(followDTO.getFollowerId());
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public DMLVo changeBlockStatus(FollowDTO followDTO) {
        //当前用户的信息是否与当前用户一致
        String userId = followDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        boolean isBlocked = followDTO.isBlocked();
        LambdaUpdateWrapper<Follow> updateWrapper = new LambdaUpdateWrapper<>();
        if(isBlocked){
            //拉黑
            updateWrapper.set(Follow::getIsBlocked, StatusEnum.ENABLE.getValue());
        }else{
            updateWrapper.set(Follow::getIsBlocked,StatusEnum.DISABLED.getValue());
        }
        updateWrapper.eq(Follow::getUserId,userId);
        updateWrapper.eq(Follow::getFollowerId, followDTO.getFollowerId());
        int result = followMapper.update(updateWrapper);
        DMLVo dmlVo = new DMLVo();
        if (result > 0 ){
            dmlVo.setId(followDTO.getFollowerId());
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public DMLVo removeFollow(FollowDTO followDTO) {
        //当前用户的信息是否与当前用户一致
        String userId = followDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        //查询原始数据
        //我关注别人
        Follow followFromMe = followMapper.selectOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, followDTO.getFollowerId()).eq(Follow::getFollowerId, followDTO.getUserId()).last("limit 1"));
        //别人关注我
        Follow followFromOther = followMapper.selectOne(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowerId, followDTO.getFollowerId()).eq(Follow::getUserId, followDTO.getUserId()).last("limit 1"));
        if(followFromMe == null || followFromMe.getFollowStatus().getValue().equals(FollowStatus.PENDING.getValue()) || followFromMe.getFollowStatus().getValue().equals(FollowStatus.REJECTED.getValue())){
            throw new BizException(ResponseCodeEnum.FOLLOW_STATUS_ERROR);
        }
        int first = followMapper.delete(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, followDTO.getFollowerId()).eq(Follow::getFollowerId, followDTO.getUserId()));

        //对方的状态为confirmed 以及状态不为拉黑才可以删除对方状态
        if(followFromOther != null && followFromOther.getFollowStatus().getValue().equals(FollowStatus.CONFIRMED.getValue()) && followFromOther.getIsBlocked().equals(StatusEnum.DISABLED.getValue())){
            int second = followMapper.delete(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowerId, followDTO.getUserId()).eq(Follow::getUserId, followDTO.getFollowerId()));
        }
        DMLVo dmlVo = new DMLVo();
        if (first > 0 ){
            dmlVo.setId(followDTO.getFollowerId());
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_DELETE_SUCCESS);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_DELETE_ERROR);
        }
        return dmlVo;
    }

    @Override
    public List<Long> highFollowedTop10() {
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id")  // 选择 user_id 字段
                .groupBy("user_id")  // 根据 user_id 分组
                .orderByDesc("COUNT(follower_id)")  // 按 follower_id 的数量降序排列
                .last("LIMIT 10");  // 限制为前 10 条

        List<Follow> follows = followMapper.selectList(queryWrapper);
        return follows.stream().map(Follow::getUserId).toList();

    }

    @Override
    public int followNumCount(Long userId,boolean isfansCount) {
        Long count ;
        if(isfansCount){
            count = followMapper.selectCount(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, userId));
        }else{
            count = followMapper.selectCount(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowerId, userId));
        }
        return Math.toIntExact(count);
    }

    @Override
    public HashMap<String, Long> queryFollowCount(String userId) {
        HashMap<String, Long> map = new HashMap<>();
        Long fansCount = followMapper.selectCount(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, userId));
        map.put("fansCount", fansCount);
        Long followingCount = followMapper.selectCount(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowerId, userId));
        map.put("followingCount", followingCount);
        return map;
    }

    @Override
    public FollowStatus getUserFollowStatus(String targetUserId, String beFollowedUserId) {
        Follow follow = followMapper.selectOne(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, targetUserId)
                .eq(Follow::getFollowerId, beFollowedUserId));
        if(follow == null){
            return FollowStatus.NONE;
        }else{
            return follow.getFollowStatus();
        }
    }

    private List<FollowerVo> filterFollowListToFollowerVoList(List<Follow> follows, boolean isGroup,boolean isCheckFollower) {
        List<FollowerVo> followerVos = new ArrayList<>();
        for (Follow follow : follows) {
            FollowerVo followerVo = filterFollowToFollowerVo(follow, isGroup,isCheckFollower);
            followerVos.add(followerVo);
        }
        return followerVos;
    }

    private FollowerVo filterFollowToFollowerVo(Follow follow, boolean isGroup, boolean isCheckFollower) {
        FollowerVo followerVo = new FollowerVo();
        Long userId = isCheckFollower ? follow.getFollowerId() : follow.getUserId();

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));

        followerVo.setFollowerId(userId);
        followerVo.setFollowerName(user.getUserName());
        followerVo.setRemark(follow.getRemark());
        followerVo.setCreateTime(follow.getCreateTime());
        followerVo.setFollowStatus(follow.getFollowStatus().getValue());

        followerVo.setIsBacked(follow.getIsBlocked());
        if (isGroup) {
            // todo 处理group 信息
        }
        return followerVo;
    }

}




