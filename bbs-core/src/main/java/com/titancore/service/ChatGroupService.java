package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.ChatGroupDTO;
import com.titancore.domain.entity.ChatGroup;
import com.titancore.domain.param.ChatGroupParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatGroupVo;


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
}
