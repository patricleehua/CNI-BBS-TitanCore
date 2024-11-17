package com.titancore.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.titancore.constant.MessageContent;
import com.titancore.constant.MessageContentType;
import com.titancore.domain.dto.*;
import com.titancore.domain.entity.*;
import com.titancore.domain.mapper.ChatGroupMapper;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.param.ChatGroupParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatGroupVo;
import com.titancore.domain.vo.DMLVo;
import com.titancore.enums.MessageType;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.SourceType;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.*;
import com.titancore.util.AuthenticationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroup>
    implements ChatGroupService {
    @Autowired
    private ChatGroupMapper chatGroupMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    @Lazy
    private ChatListService chatListService;
    @Autowired
    private ChatMessageService chatMessageService;
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
        boolean second = handleTheRelationshipBetweenUsersAndGroups(chatGroup.getId(), chatGroupDTO.getUserIds());
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

    @Override
    public DMLVo updateChatGroup(ChatGroupUpdateDTO chatGroupUpdateDTO) {
        String userId = chatGroupUpdateDTO.getOwnerUserId();
        String groupId = chatGroupUpdateDTO.getGroupId();
        AuthenticationUtil.checkUserId(userId);
        //查询旧数据
        ChatGroup chatGroup = chatGroupMapper.selectById(groupId);
        if(chatGroup==null){
            throw new BizException(ResponseCodeEnum.CHAT_GROUP_IS_NOT_EXIST);
        }
        //判断新群主是否存在可以用
        if (!chatGroupUpdateDTO.getNewOwnerUserId().isEmpty()){
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, chatGroupUpdateDTO.getNewOwnerUserId()).eq(User::getDelFlag, "0").last("limit 1"));
            if(user==null){
                throw new BizException(ResponseCodeEnum.ACCOUNT_CAN_NOT_FOUND);
            }
            chatGroup.setOwnerUserId(Long.valueOf(chatGroupUpdateDTO.getNewOwnerUserId()));
        }
        if (!chatGroupUpdateDTO.getGroupName().isEmpty()){
            chatGroup.setName(chatGroupUpdateDTO.getGroupName());
        }
        if (!chatGroupUpdateDTO.getPortrait().isEmpty()){
            chatGroup.setPortrait(chatGroupUpdateDTO.getPortrait());
        }
        if (chatGroupUpdateDTO.getIsOpen() != null){
            chatGroup.setIsOpen(chatGroupUpdateDTO.getIsOpen());
        }
        chatGroup.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(chatGroup);
        DMLVo dmlVo = new DMLVo();
        if(result){
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_SUCCESS);
            dmlVo.setId(groupId);
        }else{
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_ERROR);
            dmlVo.setId(groupId);
        }
        return dmlVo;
    }

    @Override
    public DMLVo inviteMember(ChatGroupInviteMemberDTO chatGroupInviteMemberDTO) {
        String groupId = chatGroupInviteMemberDTO.getGroupId();
        String userId = chatGroupInviteMemberDTO.getOwnerUserId();
        AuthenticationUtil.checkUserId(userId);
        ChatGroup chatGroup = chatGroupMapper.selectById(groupId);
        if(chatGroup==null){
            throw new BizException(ResponseCodeEnum.CHAT_GROUP_IS_NOT_EXIST);
        }
        boolean result = handleTheRelationshipBetweenUsersAndGroups(Long.valueOf(groupId), chatGroupInviteMemberDTO.getUserIds());
        DMLVo dmlVo = new DMLVo();
        dmlVo.setId(groupId);
        if(result){
            dmlVo.setStatus(true);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_SUCCESS);
        }else {
            dmlVo.setStatus(false);
            dmlVo.setMessage(CommonConstant.DML_UPDATE_ERROR);
        }
        return dmlVo;
    }

    @Transactional
    @Override
    public DMLVo quitChatGroup(ChatGroupQuitDTO chatGroupQuitDTO) {
        String userId = chatGroupQuitDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);

        chatGroupMemberService.remove(new LambdaQueryWrapper<ChatGroupMember>()
                .eq(ChatGroupMember::getUserId, userId)
                .eq(ChatGroupMember::getChatGroupId, chatGroupQuitDTO.getGroupId()));

        chatListService.remove(new LambdaQueryWrapper<ChatList>()
                .eq(ChatList::getFromId, userId)
                .eq(ChatList::getToId, chatGroupQuitDTO.getGroupId()));

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setFromId(userId);
        chatMessageDTO.setToId(chatGroupQuitDTO.getGroupId());
        chatMessageDTO.setSource(SourceType.GROUP.getValue());
        chatMessageDTO.setMessageType(MessageType.NOTIFY.getValue());

        ChatMessageContent chatMessageContent =  new ChatMessageContent();
        chatMessageContent.setType(MessageContentType.Quit);
        chatMessageContent.setFormUserId(userId);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        chatMessageContent.setFormUserName(user.getUserName());

        NotifyDTO notifyDTO = new NotifyDTO();
        notifyDTO.setId(chatGroupQuitDTO.getGroupId());
        notifyDTO.setType(MessageContentType.Quit);
        NotifyDTO.NotifyContent notifyContent = new NotifyDTO.NotifyContent();
                notifyContent.setTitle(MessageContent.USER_QUIET_TITLE);
                notifyContent.setText(String.format("%s%s", user.getUserName(), MessageContent.USER_QUIET_INFO));
        notifyDTO.setContent(notifyContent);

        chatMessageContent.setContent(JSON.toJSONString(notifyDTO));
        chatMessageDTO.setChatMessageContent(chatMessageContent);
        return chatMessageService.sendMessage(chatMessageDTO);
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
    private boolean handleTheRelationshipBetweenUsersAndGroups(Long groupId, List<String> userIds){
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO();
        chatMemberDTO.setUserIds(userIds);
        chatMemberDTO.setChatGroupId(String.valueOf(groupId));
        return chatGroupMemberService.addMemberListToGroupByGroupId(chatMemberDTO);
    }

}




