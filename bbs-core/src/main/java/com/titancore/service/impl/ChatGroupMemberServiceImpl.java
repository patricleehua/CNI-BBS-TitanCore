package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.titancore.domain.dto.ChatMemberDTO;
import com.titancore.domain.entity.ChatGroupMember;
import com.titancore.domain.entity.User;
import com.titancore.domain.mapper.ChatGroupMemberMapper;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.param.ChatMemberParam;
import com.titancore.domain.param.PageResult;
import com.titancore.service.ChatGroupMemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class ChatGroupMemberServiceImpl extends ServiceImpl<ChatGroupMemberMapper, ChatGroupMember>
    implements ChatGroupMemberService {

    private final ChatGroupMemberMapper chatGroupMemberMapper;
    private final UserMapper userMapper;
    @Override
    public PageResult memberList(ChatMemberParam chatMemberParam) {
        Page<ChatGroupMember> page = new Page<>(chatMemberParam.getPageNo(),chatMemberParam.getPageSize());
        LambdaQueryWrapper<ChatGroupMember> queryWrapper = new LambdaQueryWrapper<ChatGroupMember>().eq(ChatGroupMember::getChatGroupId, chatMemberParam.getChatGroupId());

        Page<ChatGroupMember> chatGroupMemberPage = chatGroupMemberMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        //todo 处理返回的ChatGroupMember 对象
        pageResult.setTotal(chatGroupMemberPage.getTotal());
        pageResult.setRecords(chatGroupMemberPage.getRecords());
        return pageResult;
    }
    @Override
    public boolean addMemberToGroupByGroupId(ChatMemberDTO chatMemberDTO) {
        User user = userMapper.selectById(chatMemberDTO.getUserId());
        ChatGroupMember chatGroupMember = new ChatGroupMember();
        chatGroupMember.setUserId(Long.valueOf(chatMemberDTO.getUserId()));
        chatGroupMember.setChatGroupId(Long.valueOf(chatMemberDTO.getChatGroupId()));
        chatGroupMember.setGroupName(user.getUserName());
        int result = chatGroupMemberMapper.insert(chatGroupMember);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean addMemberListToGroupByGroupId(ChatMemberDTO chatMemberDTO) {
        String chatGroupId = chatMemberDTO.getChatGroupId();
        List<String> userIds = chatMemberDTO.getUserIds();
        if (userIds.isEmpty()) return false;
        List<String> newUserIds = userIds.stream()
                .filter(userId -> {
                    boolean exists = chatGroupMemberMapper.exists(new LambdaQueryWrapper<ChatGroupMember>()
                            .eq(ChatGroupMember::getUserId, userId)
                            .eq(ChatGroupMember::getChatGroupId, chatGroupId));
                    return !exists;
                })
                .toList();
        if (newUserIds.isEmpty()) return false;
        List<ChatGroupMember> chatGroupMemberList = queryUserInfo(newUserIds).stream().map(user -> {
            ChatGroupMember chatGroupMember = new ChatGroupMember();
            chatGroupMember.setChatGroupId(Long.valueOf(chatGroupId));
            chatGroupMember.setGroupName(user.getUserName());
            chatGroupMember.setUserId(user.getUserId());
            return chatGroupMember;
        }).toList();
        return super.saveBatch(chatGroupMemberList);
    }

    @Override
    public boolean deleteMemberByChatGroupId(String groupId) {
        int result = chatGroupMemberMapper.delete(new LambdaQueryWrapper<ChatGroupMember>().eq(ChatGroupMember::getChatGroupId, groupId));
        return result>0;
    }

    @Override
    public List<ChatGroupMember> getGroupMemberByGroupId(String groupId) {
        return chatGroupMemberMapper.selectList(new LambdaQueryWrapper<ChatGroupMember>().eq(ChatGroupMember::getChatGroupId, groupId));
    }

    @Override
    public boolean isMemberExists(String groupId, String userId) {
        LambdaQueryWrapper<ChatGroupMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatGroupMember::getUserId, userId)
                .eq(ChatGroupMember::getChatGroupId, groupId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<Long> getGroupIdsByUserId(String userId) {
        if (userId != null) {
            return chatGroupMemberMapper.selectList(new LambdaQueryWrapper<ChatGroupMember>().eq(ChatGroupMember::getUserId, userId)).stream().map(ChatGroupMember::getChatGroupId).toList();
        }
        return List.of();
    }

    private List<User> queryUserInfo(List<String> userIds){
        return userMapper.selectBatchIds(userIds);
    }


}




