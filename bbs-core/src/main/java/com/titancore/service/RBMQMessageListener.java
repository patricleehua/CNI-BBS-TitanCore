package com.titancore.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.lang.reflect.Type;

import com.rabbitmq.client.Channel;
import com.titancore.config.rabbitmq.RabbitMqConstant;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.domain.entity.User;
import com.titancore.enums.SourceType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;



@Component
@Slf4j
public class RBMQMessageListener {

    @Autowired
    private UserService userService;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private ChatListService chatListService;
    @Autowired
    private WebSocketService webSocketService;

    /**
     * 监听私聊用户队列
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = RabbitMqConstant.QUEUE_TOPIC_PRIVATE_CHAT)
    public void receiveMessageFromPrivateChat(Message message, Channel channel) {
        try {
            String jsonString = JSON.parseObject(new String(message.getBody())).toString();
            Type type = new TypeReference<ChatMessageDTO>(){}.getType();
            ChatMessageDTO chatMessageDTO = JSON.parseObject(jsonString, type);

            //获取发送方用户信息
            User user = userService.getById(chatMessageDTO.getFromId());
            //保存消息
            ChatMessage chatMessage = chatMessageService.saveChatMessage(user,chatMessageDTO);
            //更新聊天列表
            if (chatMessage != null) {
                chatListService.updateChatList(String.valueOf(chatMessage.getFromId()),String.valueOf( chatMessage.getToId()),chatMessage.getChatMessageContent(), SourceType.USER);
            }

            //发送消息
            if (null != chatMessage) {
                webSocketService.sendMsgToUser(chatMessage, String.valueOf(chatMessage.getToId()));
            }

            log.info("Received message: {}", chatMessage);
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);

            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error processing message", e);
            // 将消息标记为未确认并进入死信队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConstant.QUEUE_DEAD_PRIVATE_CHAT)
    public void receiveMessageDlq(Message message, Channel channel) {
        String jsonString = JSON.parseObject(new String(message.getBody())).toString();
        Type type = new TypeReference<ChatMessage>(){}.getType();
        ChatMessage chatMessage = JSON.parseObject(jsonString, type);

        // 处理消息（例如，保存到数据库）
        int i = 1 / 0; // 故意抛出异常
        // 处理死信消息（例如，保存到数据库日志）
        log.info("receiveMessageDlq message: {}", chatMessage);
        // 将处理结果通过 STOMP 广播到前端
//        messagingTemplate.convertAndSend("/topic/public", chatMessage);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
