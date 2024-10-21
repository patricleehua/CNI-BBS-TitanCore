package com.titancore.service;

import com.alibaba.fastjson.JSON;
import com.titancore.config.rabbitmq.RabbitMqConstant;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.rabbitmq.util.RabbitMqHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class RBMQProducerService {

    @Autowired
    private RabbitMqHelper rabbitMqHelper;

    /**
     * 发送可重试消息给用户
     */

    public void sendMsgToUser(ChatMessageDTO chatMessageDTO) {
        try {
            String jsonMessage = JSON.toJSONString(chatMessageDTO);
            rabbitMqHelper.sendMessageRetry(RabbitMqConstant.EXCHANGE_TOPIC, RabbitMqConstant.ROUTING_TOPIC_PRIVATE_CHAT, jsonMessage);
        } catch (Exception e) {
            log.error("发送可重试消息失败:{}", e.getMessage());
            throw new BizException(ResponseCodeEnum.MESSAGE_QUEUE_SEND_MESSAGE_TO_USER_ERROR);
        }
    }
}
