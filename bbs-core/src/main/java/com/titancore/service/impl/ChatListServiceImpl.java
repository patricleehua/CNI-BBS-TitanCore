package com.titancore.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.titancore.domain.dto.ChatListDTO;
import com.titancore.domain.entity.ChatGroupMember;
import com.titancore.domain.entity.ChatList;
import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.domain.mapper.ChatListMapper;
import com.titancore.domain.param.ChatListParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatListVo;
import com.titancore.enums.LevelType;
import com.titancore.enums.MessageType;
import com.titancore.enums.SourceType;
import com.titancore.service.ChatGroupMemberService;
import com.titancore.service.ChatListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatList>
    implements ChatListService {

    @Autowired
    private ChatListMapper chatListMapper;

    @Override
    public void updateChatList(String fromId, String toId, ChatMessageContent chatMessageContent, SourceType sourceType) {
        //todo 判断聊天列表是否存在
        ChatList chatList = chatListMapper.selectOne(new LambdaQueryWrapper<ChatList>()
                .eq(ChatList::getToId, toId).eq(ChatList::getFromId, fromId));

        if (null == chatList) {
            //新建
            chatList = new ChatList();
            chatList.setIsTop(false);
            chatList.setFromId(Long.valueOf(fromId));
            chatList.setToId(Long.valueOf(toId));
            chatList.setSourceType(sourceType);
            if (fromId.equals(chatMessageContent.getFormUserId())) {
                chatList.setUnreadNum(0);
            } else {
                chatList.setUnreadNum(1);
            }
            chatList.setLastMsgContent(chatMessageContent);
            chatListMapper.insert(chatList);
        } else {
            //更新
            if (fromId.equals(chatMessageContent.getFormUserId())) {
                chatList.setUnreadNum(0);
            } else {
                chatList.setUnreadNum(chatList.getUnreadNum() + 1);
            }
            chatList.setLastMsgContent(chatMessageContent);
            chatListMapper.updateById(chatList);
            //更新自己的聊天列表
            LambdaUpdateWrapper<ChatList> updateWrapper = new LambdaUpdateWrapper<ChatList>()
                    .set(ChatList::getLastMsgContent, JSONUtil.toJsonStr(chatMessageContent))
                    .eq(ChatList::getFromId, fromId)
                    .eq(ChatList::getFromId, toId);
            chatListMapper.update(updateWrapper);
        }
    }
    @Autowired
    private ChatGroupMemberService chatGroupMemberService;
    @Override
    public void updateChatListGroup(String groupId, ChatMessageContent chatMessageContent) {
        List<ChatGroupMember> members = chatGroupMemberService.getGroupMemberByGroupId(groupId);
        for (ChatGroupMember member : members) {
            updateChatList(String.valueOf(member.getUserId()), groupId, chatMessageContent, SourceType.GROUP);
        }
    }

    @Override
    public PageResult queryChatList(ChatListParam chatListParam) {
        // todo 登入鉴权
        Page<ChatList> page = new Page<>(chatListParam.getPageNo(), chatListParam.getPageSize());
        LambdaQueryWrapper<ChatList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatList::getFromId, chatListParam.getFromId());
        Page<ChatList> chatListPage = chatListMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(chatListPage.getTotal());
        pageResult.setRecords(chatListPage.getRecords());
        return pageResult;
    }

    @Override
    public ChatListVo createChatList(ChatListDTO chatListDTO) {
        //todo 登录鉴权 群组创建时/群友发送信息时，自动跟新  最后消息内容 完善异常处理

        ChatList chatList = new ChatList();
        SourceType sourceType = SourceType.valueOfAll(chatListDTO.getSource());
        if (sourceType != null){
            switch (sourceType){
                case USER -> {chatList.setSourceType(SourceType.USER);}
                case GROUP -> {chatList.setSourceType(SourceType.GROUP);}
                default -> throw new RuntimeException("不支持的聊天类型");
            }
        }
        chatList.setFromId(Long.valueOf(chatListDTO.getFromId()));
        chatList.setToId(Long.valueOf(chatListDTO.getToId()));
        int result = chatListMapper.insert(chatList);
        ChatListVo chatListVo = new ChatListVo();
        if(result >0 ){
            chatListVo.setStatus(true);
            chatListVo.setChatListId(String.valueOf(chatList.getId()));
            chatListVo.setMessage("创建成功");
        }else{
            chatListVo.setStatus(false);
            chatListVo.setMessage("创建失败");
        }
        return chatListVo;
    }

    @Override
    public ChatListVo deleteChatList(ChatListDTO chatListDTO) {
        //1、鉴权
        //2、删除类别
        SourceType sourceType = SourceType.valueOfAll(chatListDTO.getSource());
        LambdaQueryWrapper<ChatList> queryWrapper = new LambdaQueryWrapper<ChatList>().eq(ChatList::getId, chatListDTO.getChatListId());
        int result = 0;
        if (sourceType != null){
            switch (sourceType){
                case USER -> {
                    //todo 判读是否为当前用户 只能删除自己的消息
                    queryWrapper.eq(ChatList::getSourceType, SourceType.USER);
                    result = chatListMapper.delete(queryWrapper);
                }
                case GROUP -> {
                    //todo 判读是否为群主
                    queryWrapper.eq(ChatList::getSourceType, SourceType.GROUP);
                    result = chatListMapper.delete(queryWrapper);
                }
                default -> throw new RuntimeException("不支持的聊天类型");
            }
        }

        return null;
    }
}



