package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.ChatListDTO;
import com.titancore.domain.dto.SetTopForChatListDTO;
import com.titancore.domain.entity.ChatList;
import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.domain.param.ChatListParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatListDmlVo;
import com.titancore.domain.vo.ChatListVo;
import com.titancore.enums.SourceType;


public interface ChatListService extends IService<ChatList> {
    /**
     * 根据fromId会话ID更新相关的会话列表
     * @param fromId
     * @param toId
     * @param msgContent
     * @param sourceType
     */
    void updateChatList(String fromId, String toId, ChatMessageContent msgContent, SourceType sourceType);

    /**
     * 根据groupId更新群组在列表的展示
     * @param groupId
     * @param chatMessageContent
     */
    void updateChatListGroup(String groupId, ChatMessageContent chatMessageContent);

    /**
     * 根据fromId 查询对应的聊天列表
     * @param chatListParam
     * @return
     */
    PageResult queryChatList(ChatListParam chatListParam);

    /**
     * 通过 from id 和 to id 获取聊天列表
     * @param fromId
     * @param toId
     * @return
     */
    ChatList getChatListByFromIdAndToId(String fromId, String toId);
    /**
     * 建立聊天对话
     * @param chatListDTO
     * @return
     */

    ChatListDmlVo createChatList(ChatListDTO chatListDTO);
    /**
     * 删除聊天对话
     * @param chatListDTO
     * @return
     */
    ChatListDmlVo deleteChatList(ChatListDTO chatListDTO);

    /**
     * 聊天列表置顶
     * @param setTopChatListDTO
     * @return
     */
    ChatListDmlVo setTopChatList(SetTopForChatListDTO setTopChatListDTO);

    /**
     * 消息已读
     * @param fromId
     * @param toId
     * @return
     */
    ChatListDmlVo messageRead(String fromId, String toId);

    /**
     * 全部已读
     * @param userId
     * @return
     */
    ChatListDmlVo messageReadAll(String userId);

    /**
     * 获取聊天列表详细信息
     * @param chatListDTO
     * @return
     */
    ChatListVo detailChartList(ChatListDTO chatListDTO);
}
