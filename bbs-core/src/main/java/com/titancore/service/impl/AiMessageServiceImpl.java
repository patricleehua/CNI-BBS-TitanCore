package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.AiMessageDTO;
import com.titancore.domain.entity.AiMessage;
import com.titancore.domain.entity.AiMessageMedia;
import com.titancore.domain.mapper.AiMessageMapper;
import com.titancore.domain.vo.AiMessageVo;
import com.titancore.enums.AiMessageType;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.AiMessageService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* @author leehua
* @description 针对表【ai_message(聊天记录表)】的数据库操作Service实现
* @createDate 2024-12-24 14:24:36
*/
@Service
public class AiMessageServiceImpl extends ServiceImpl<AiMessageMapper, AiMessage>
    implements AiMessageService {

    @Override
    public List<AiMessage> getAiMessageListBySessionId(String sessionId, int lastN) {
        AiMessageMapper aiMessageMapper = this.getBaseMapper();
        LambdaQueryWrapper<AiMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiMessage::getAiSessionId, sessionId);
        queryWrapper.orderBy(true, false, AiMessage::getCreateTime);
        queryWrapper.last("limit "+lastN);
        return aiMessageMapper.selectList(queryWrapper);
    }

    @Override
    public List<AiMessageVo> getAiMessageListVoBySessionId(String conversationId, int lastN) {
        AiMessageMapper aiMessageMapper = this.getBaseMapper();
        LambdaQueryWrapper<AiMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiMessage::getAiSessionId, conversationId);
        queryWrapper.orderBy(true, false, AiMessage::getCreateTime);
        queryWrapper.last("limit "+lastN);
        List<AiMessage> aiMessages = aiMessageMapper.selectList(queryWrapper);
        Collections.reverse(aiMessages);
        return  aiMessages.stream().map(this::convertToAiMessageVo).toList();
    }

    @Override
    public boolean deleteSessionMessageBySessionId(String sessionId) {
        int delete = this.getBaseMapper()
                .delete(new LambdaQueryWrapper<AiMessage>().eq(AiMessage::getAiSessionId, sessionId));
        return delete > 0;
    }

    @Override
    public boolean saveAiMessage(AiMessageDTO aiMessageDTO) {
        AiMessage aiMessage = convertToAiMessage(aiMessageDTO);
        return this.save(aiMessage);
    }

    @Override
    public AiMessage convertToAiMessage(AiMessageDTO aiMessageDTO) {
        AiMessage aiMessage = new AiMessage();
        aiMessage.setAiSessionId(Long.valueOf(aiMessageDTO.getAiSessionId()));
        aiMessage.setCreatorId(Long.valueOf(aiMessageDTO.getCreatorId()));
        aiMessage.setEditorId(Long.valueOf(aiMessageDTO.getCreatorId()));
        aiMessage.setTextContent(aiMessageDTO.getTextContent());
        AiMessageType aiMessageType = AiMessageType.valueOfAll(aiMessageDTO.getType());
        if (aiMessageType != null){
            aiMessage.setType(aiMessageType);
        }else{
            throw new BizException(ResponseCodeEnum.AI_MESSAGE_TYPE_IS_ERROR);
        }

        Map<String, String> medias = aiMessageDTO.getMedias();
        if (medias != null) {
            List<AiMessageMedia> mediaList = new ArrayList<>();
            for (Map.Entry<String, String> entry : medias.entrySet()) {
                AiMessageMedia media = new AiMessageMedia();
                media.setType(entry.getKey());
                media.setUrl(entry.getValue());
                mediaList.add(media);
            }
            aiMessage.setMedias(mediaList);
        }


        return aiMessage;
    }

    @Override
    public AiMessage convertToAiMessage(Message message) {
        if (message != null){
            AiMessage aiMessage = new AiMessage();
            aiMessage.setTextContent(message.getContent());
            MessageType messageType = message.getMessageType();
            switch(messageType) {
                case USER -> aiMessage.setType(AiMessageType.USER);
                case ASSISTANT ->aiMessage.setType(AiMessageType.ASSISTANT);
                case SYSTEM -> aiMessage.setType(AiMessageType.SYSTEM);
                default ->
                        throw new BizException(ResponseCodeEnum.AI_MESSAGE_TYPE_IS_ERROR);
            }
            return aiMessage;
        }
        return null;
    }

    private AiMessageVo convertToAiMessageVo(AiMessage aiMessage) {
        AiMessageVo aiMessageVo = new AiMessageVo();
        BeanUtils.copyProperties(aiMessage, aiMessageVo);
        aiMessageVo.setId(String.valueOf(aiMessage.getId()));
        aiMessageVo.setAiSessionId(String.valueOf(aiMessage.getId()));
        aiMessageVo.setCreatorId(String.valueOf(aiMessage.getId()));
        return aiMessageVo;
    }

}




