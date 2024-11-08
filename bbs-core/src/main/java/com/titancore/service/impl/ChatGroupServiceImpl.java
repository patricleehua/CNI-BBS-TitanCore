package com.titancore.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.titancore.domain.dto.ChatGroupDTO;
import com.titancore.domain.dto.ChatMemberDTO;
import com.titancore.domain.entity.ChatGroup;
import com.titancore.domain.mapper.ChatGroupMapper;
import com.titancore.domain.param.ChatGroupParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatGroupVo;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.RoleType;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.ChatGroupMemberService;
import com.titancore.service.ChatGroupNoticeService;
import com.titancore.service.ChatGroupService;
import com.titancore.util.AuthenticationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroup>
    implements ChatGroupService {
    @Autowired
    private ChatGroupMapper chatGroupMapper;

    @Override
    public PageResult chatGroupList(ChatGroupParam chatGroupParam) {
        Page<ChatGroup> page = new Page<>(chatGroupParam.getPageNo(), chatGroupParam.getPageSize());
        LambdaQueryWrapper<ChatGroup> wrapper = new LambdaQueryWrapper<ChatGroup>().eq(ChatGroup::getIsOpen, 0);

        if (StringUtils.isNotBlank(chatGroupParam.getId())) {
            wrapper.eq(ChatGroup::getId,chatGroupParam.getId());
        }
        if (StringUtils.isNotBlank(chatGroupParam.getGroupName())) {
            wrapper.eq(ChatGroup::getName,chatGroupParam.getGroupName());
        }
        if (StringUtils.isNotBlank(chatGroupParam.getUserId())) {
            wrapper.eq(ChatGroup::getUserId,chatGroupParam.getUserId());
        }

        Page<ChatGroup> chatGroupPage = chatGroupMapper.selectPage(page, wrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(chatGroupPage.getTotal());
        pageResult.setRecords(chatGroupPage.getRecords());
        return pageResult;
    }

    @Autowired
    private ChatGroupMemberService chatGroupMemberService;
    @Override
    @Transactional
    public ChatGroupVo createChatGroup(ChatGroupDTO chatGroupDTO) {
        String userId = chatGroupDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        //todo 一个用户一天只能创建三个群组 也许还需要规划人数
        ChatGroup chatGroup = this.copy(chatGroupDTO);
        int first = chatGroupMapper.insert(chatGroup);
        //todo 建立用户群组关系
        boolean second = handleTheRelationshipBetweenUsersAndGroups(chatGroup.getId(), chatGroupDTO);
        //todo 创建聊天列表 仅为自己，只有当创建者 发送第一条消息才会给群成员创建他们的聊天列表
        ChatGroupVo chatGroupVo = new ChatGroupVo();
        //todo 优化响应结果
        if(first>0 || second){
            chatGroupVo.setGroupId(String.valueOf(chatGroup.getId()));
            chatGroupVo.setStatus(true);
        }else{
            chatGroupVo.setStatus(false);
        }
        return chatGroupVo;
    }
    @Autowired
    private ChatGroupNoticeService chatGroupNoticeService;
    @Override
    @Transactional
    public ChatGroupVo dissolveChatGroup(ChatGroupDTO chatGroupDTO) {

        String userId = chatGroupDTO.getOwnerUserId();
        AuthenticationUtil.checkUserId(userId);
        //断当前用户是否等于群主 抛出异常
        ChatGroup chatGroup = chatGroupMapper.selectOne(new LambdaQueryWrapper<ChatGroup>().eq(ChatGroup::getOwnerUserId, userId).eq(ChatGroup::getId,chatGroupDTO.getGroupId()));
        if(chatGroup==null){
            throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_IS_DIFFERENT);
        }

        //1、删除历史公告
        boolean first = chatGroupNoticeService.deleteNoticeByGroupId(chatGroupDTO.getGroupId());
        //2、删除用户群组关系
        boolean second = chatGroupMemberService.deleteMemberByChatGroupId(chatGroupDTO.getGroupId());
        //3、删除群组历史保存聊天
        //todo
        //4、删除群组
        int fourth = chatGroupMapper.deleteById(chatGroupDTO.getGroupId());
        //5、通知用户被解散
        //todo 消息队列 优化响应结果
        ChatGroupVo chatGroupVo = new ChatGroupVo();
        if(second && fourth>0){
            chatGroupVo.setStatus(true);
            chatGroupVo.setMessage("群组解散成功");
            chatGroupVo.setGroupId(chatGroupDTO.getGroupId());
        }else{
            chatGroupVo.setStatus(false);
            chatGroupVo.setMessage("群组解散失败");
            chatGroupVo.setGroupId(chatGroupDTO.getGroupId());
        }

        return chatGroupVo;
    }

    @Override
    public ChatGroup getGroupNameByGroupId(Long toId) {
        return chatGroupMapper.selectById(toId);
    }

    private ChatGroup copy(ChatGroupDTO chatGroupDTO) {

        ChatGroup chatGroup = new ChatGroup();
        if (chatGroupDTO.getUserId() != null) {
            chatGroup.setUserId(Long.valueOf(chatGroupDTO.getUserId()));
            chatGroup.setOwnerUserId(Long.valueOf(chatGroupDTO.getOwnerUserId()));
        }
        //todo 初始群聊头像
        chatGroup.setPortrait("http://www.baidu.com");
        chatGroup.setName(chatGroupDTO.getGroupName() != null ? chatGroupDTO.getGroupName() : "CNI-GROUP"+ IdUtil.randomUUID().substring(0, 8));

        if (chatGroupDTO.getUserIds() != null) {
            chatGroup.setMemberNum(chatGroupDTO.getUserIds().size());
        }

        chatGroup.setIsOpen(chatGroupDTO.getIsOpen());

        return chatGroup;
    }
    private boolean handleTheRelationshipBetweenUsersAndGroups(Long groupId,ChatGroupDTO chatGroupDTO){
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO();
        chatMemberDTO.setUserIds(chatGroupDTO.getUserIds());
        chatMemberDTO.setChatGroupId(String.valueOf(groupId));
        return chatGroupMemberService.addMemberListToGroupByGroupId(chatMemberDTO);
    }

}




