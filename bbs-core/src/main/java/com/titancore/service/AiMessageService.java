package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.AiMessageDTO;
import com.titancore.domain.entity.AiMessage;
import com.titancore.domain.vo.AiMessageVo;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
* @author leehua
* @description 针对表【ai_message(聊天记录表)】的数据库操作Service
* @createDate 2024-12-24 14:24:36
*/
public interface AiMessageService extends IService<AiMessage> {
    /**
     * 根据会话id查询最近N条数据 (提供给语言模型）
     * @param sessionId 会话Id
     * @param lastN 最后N条
     * @return
     */
    List<AiMessage> getAiMessageListBySessionId(String sessionId,int lastN);

    /**
     *  根据会话id查询最近N条数据 (提供给前端用户历史记录展示）
     * @param conversationId
     * @param lastN
     * @return
     */
    List<AiMessageVo> getAiMessageListVoBySessionId(String conversationId, int lastN);

    boolean deleteSessionMessageBySessionId(String sessionId);

    boolean saveAiMessage(AiMessageDTO aiMessageDTO);

    AiMessage convertToAiMessage(AiMessageDTO aiMessageDTO) ;

    AiMessage convertToAiMessage(Message message) ;
}
