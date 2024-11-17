package com.titancore.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.titancore.domain.dto.ChatListDTO;
import com.titancore.domain.entity.ChatGroupMember;
import com.titancore.domain.entity.ChatList;
import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.domain.mapper.ChatListMapper;
import com.titancore.domain.mapper.UserMapper;
import com.titancore.domain.param.ChatListParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatListDmlVo;
import com.titancore.domain.vo.ChatListVo;
import com.titancore.enums.SourceType;
import com.titancore.service.ChatGroupMemberService;
import com.titancore.service.ChatGroupService;
import com.titancore.service.ChatListService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatList>
    implements ChatListService {

    @Autowired
    private ChatListMapper chatListMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void updateChatList(String fromId, String toId, ChatMessageContent chatMessageContent, SourceType sourceType) {
        //todo 判断聊天列表是否存在
        ChatList chatList = chatListMapper.selectOne(new LambdaQueryWrapper<ChatList>()
                .eq(ChatList::getToId, toId).eq(ChatList::getFromId, fromId).eq(ChatList::getSourceType, sourceType));

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
                    .set(ChatList::getLastMsgContent, JSON.toJSONString(chatMessageContent))
                    .eq(ChatList::getFromId, fromId)
                    .eq(ChatList::getFromId, toId);
            chatListMapper.update(updateWrapper);
        }
    }
    @Autowired
    private ChatGroupMemberService chatGroupMemberService;
    @Autowired
    @Lazy
    private ChatGroupService cacheGroupService;
    @Override
    public void updateChatListGroup(String groupId, ChatMessageContent chatMessageContent) {
        List<ChatGroupMember> members = chatGroupMemberService.getGroupMemberByGroupId(groupId);
        for (ChatGroupMember member : members) {
            updateChatList(String.valueOf(member.getUserId()), groupId, chatMessageContent, SourceType.GROUP);
        }
    }

    @Override
    public PageResult queryChatList(ChatListParam chatListParam) {
        // todo 登入鉴权 解决列表为空
        Page<ChatList> page = new Page<>(chatListParam.getPageNo(), chatListParam.getPageSize());
        LambdaQueryWrapper<ChatList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatList::getFromId, chatListParam.getFromId());
        //群组
        queryWrapper.or().eq(ChatList::getToId,chatListParam.getFromId()).eq(ChatList::getSourceType, SourceType.GROUP);
        Page<ChatList> chatListPage = chatListMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(chatListPage.getTotal());
        List<ChatListVo> chatListVoListDml = filterChatListToChatListVo(chatListPage.getRecords());
        pageResult.setRecords(chatListVoListDml);
        return pageResult;
    }

    @Override
    public ChatList getChatListByFromIdAndToId(String fromId, String toId) {
        return chatListMapper.selectOne(new LambdaQueryWrapper<ChatList>().eq(ChatList::getFromId, fromId).eq(ChatList::getToId, toId));
    }

    /**
     * ChatLists 转 ChatListVos对象
     * @param chatLists
     * @return
     */
    private List<ChatListVo> filterChatListToChatListVo(List<ChatList> chatLists) {
        return chatLists.stream().map(chatList -> {
            ChatListVo chatListVo = new ChatListVo();
            BeanUtils.copyProperties(chatList,chatListVo);
            chatListVo.setId(String.valueOf(chatList.getId()));
            chatListVo.setFromId(String.valueOf(chatList.getFromId()));
            chatListVo.setToId(String.valueOf(chatList.getToId()));
            SourceType sourceType = SourceType.valueOfAll(chatList.getSourceType().getValue());
            if(sourceType!=null){
                switch (sourceType) {
                    case USER -> {
                        chatListVo.setToName(userMapper.selectById(chatList.getToId()).getUserName());
                    }
                    case GROUP -> {
                        chatListVo.setToName(cacheGroupService.getGroupNameByGroupId(chatList.getToId()).getName());
                    }
                    case SYSTEM -> {
                        chatListVo.setToName("系统消息");
                    }
                }
                chatListVo.setSourceType(sourceType.getValue());
            }
            return chatListVo;
        }).toList();
    }

    @Override
    public ChatListDmlVo createChatList(ChatListDTO chatListDTO) {
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
        ChatListDmlVo chatListDmlVo = new ChatListDmlVo();
        if(result >0 ){
            chatListDmlVo.setStatus(true);
            chatListDmlVo.setChatListId(String.valueOf(chatList.getId()));
            chatListDmlVo.setMessage("创建成功");
        }else{
            chatListDmlVo.setStatus(false);
            chatListDmlVo.setMessage("创建失败");
        }
        return chatListDmlVo;
    }

    @Override
    public ChatListDmlVo deleteChatList(ChatListDTO chatListDTO) {
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




