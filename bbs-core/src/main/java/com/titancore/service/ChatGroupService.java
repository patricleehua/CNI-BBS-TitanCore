package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.*;
import com.titancore.domain.entity.ChatGroup;
import com.titancore.domain.param.ChatGroupParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatGroupDetailsVo;
import com.titancore.domain.vo.ChatGroupVo;
import com.titancore.domain.vo.DMLVo;


public interface ChatGroupService  extends IService<ChatGroup> {
    /**
     * todo
     * 查询群聊列表
     * @param chatGroupParam
     * @return
     */
    PageResult chatGroupList(ChatGroupParam chatGroupParam);

    /**
     * 创建群聊
     * @param chatGroupDTO
     * @return
     */

    ChatGroupVo createChatGroup(ChatGroupDTO chatGroupDTO);

    /**
     * 解散群聊
     * @param chatGroupDTO
     * @return
     */
    ChatGroupVo dissolveChatGroup(ChatGroupDTO chatGroupDTO);

    /**
     * 根据群号查询群组信息
     * @param fromId
     * @return
     */
    ChatGroup getGroupNameByGroupId(Long fromId);

    /**
     * 根据群号更新群组信息
     * @param chatGroupUpdateDTO
     * @return
     */
    DMLVo updateChatGroup(ChatGroupUpdateDTO chatGroupUpdateDTO);

    /**
     * 根据群ID邀请新成员[]
     * @param chatGroupInviteMemberDTO
     * @return
     */
    DMLVo inviteMember(ChatGroupInviteMemberDTO chatGroupInviteMemberDTO);

    /**
     * 退出群聊
     * @param chatGroupQuitDTO
     * @return
     */
    DMLVo quitChatGroup(ChatGroupQuitDTO chatGroupQuitDTO);

    /**
     * 踢出群聊
     * @param chatGroupKickDTO
     * @return
     */
    boolean kickChatGroup(ChatGroupKickDTO chatGroupKickDTO);

    /**
     * 转移群主
     * @param chatGroupTransferDTO
     * @return
     */
    ChatGroupVo transferChatGroup(ChatGroupTransferDTO chatGroupTransferDTO);

    /**
     * 群详情
     * @param groupId
     * @return
     */
    ChatGroupDetailsVo detailsChatGroup(String groupId);
}
