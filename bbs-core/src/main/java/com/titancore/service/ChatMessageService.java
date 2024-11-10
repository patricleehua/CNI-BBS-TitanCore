package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.dto.ReeditDTO;
import com.titancore.domain.dto.RetractionDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.domain.entity.User;
import com.titancore.domain.param.ChatMessageParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatMessageRetractionVo;
import com.titancore.domain.vo.DMLVo;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 撤回消息内容
     * @param reeditDTO
     * @return
     */
    ChatMessageRetractionVo reeditMessage(ReeditDTO reeditDTO);

    /**
     * 撤回消息
     * @param retractionDTO
     * @return
     */
    ChatMessage retractionMessage(RetractionDTO retractionDTO);

    /**
     * 历史聊天记录
     * @param chatMessageParam
     * @return
     */
    PageResult messageRecord(ChatMessageParam chatMessageParam);

    /**
     * 发送文件
     * @param file 文件
     * @param userId 用户ID
     * @param msgId 预存消息ID
     * @return
     */
    String sendFileOnMsgId(MultipartFile file, String userId, String msgId);

    /**
     * 获取文件消息内容
     * @param userId
     * @param msgId
     * @return
     */
    ChatMessageContent getFileMsgContent(String userId, String msgId);
}
