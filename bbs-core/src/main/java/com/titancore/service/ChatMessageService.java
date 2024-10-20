package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.domain.entity.User;
import com.titancore.domain.vo.DMLVo;

public interface ChatMessageService extends IService<ChatMessage> {
    /**
     * 发送信息
     * @param chatMessageDTO
     * @return
     */
    DMLVo sendMessage(ChatMessageDTO chatMessageDTO);

    /**
     * 保存信息到数据库
     * @param user
     * @param chatMessageDTO
     * @return
     */
    ChatMessage saveChatMessage(User user, ChatMessageDTO chatMessageDTO) ;
}
