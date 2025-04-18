package com.titancore.service;


import com.alibaba.fastjson.JSON;

import com.rabbitmq.client.Channel;
import com.titancore.config.rabbitmq.RabbitMqConstant;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.domain.entity.User;
import com.titancore.enums.MessageFormat;
import com.titancore.enums.MessageType;
import com.titancore.enums.SourceType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


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
    @Autowired
    private FollowService followService;
    @Autowired
    private ChatGroupMemberService chatGroupMemberService;

    /**
     * 监听私聊用户队列
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = RabbitMqConstant.QUEUE_TOPIC_PRIVATE_CHAT)
    public void receiveMessageFromPrivateChat(Message message, Channel channel) {
        try {
            ChatMessageDTO chatMessageDTO = messageParseToChatMessageDTO(message);
            //获取发送方用户信息
            User user = userService.getById(chatMessageDTO.getFromId());
            //检查关系
            Boolean status = followService.queryFollowStatus(chatMessageDTO.getFromId(), chatMessageDTO.getToId());
            if (!status) {
                log.info("用户{}与用户{}关系为{}，发送失败", chatMessageDTO.getFromId(), chatMessageDTO.getToId(), status);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setFromId(1L);
                chatMessage.setToId(Long.valueOf(chatMessageDTO.getFromId()));
                chatMessage.setMessageFormat(MessageFormat.TEXT);
                chatMessage.setMessageType(MessageType.NOTIFY);
                chatMessage.setCreateTime(LocalDateTime.now());
                chatMessage.setUpdateTime(LocalDateTime.now());
                chatMessage.setSourceType(SourceType.SYSTEM);
                ChatMessageContent content = new ChatMessageContent();
                content.setContent("用户" + user.getUserName() + "不在与您" + chatMessageDTO.getToId() + "的好友关系中，发送失败");
                chatMessage.setChatMessageContent(content);
                webSocketService.sendMsgToUser(chatMessage, String.valueOf(chatMessageDTO.getFromId()));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
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
    public void receiveMessageDlqFromPrivateChat(Message message, Channel channel) {
        ChatMessageDTO chatMessageDTO = messageParseToChatMessageDTO(message);

        // 处理消息（例如，保存到数据库）

        // 处理死信消息（例如，保存到数据库日志）
        log.info("receiveMessageDlq message: {}", chatMessageDTO);
        // 将处理结果通过 STOMP 广播到前端
//        messagingTemplate.convertAndSend("/topic/public", chatMessage);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听群组队列
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = RabbitMqConstant.QUEUE_TOPIC_GROUP_CHAT)
    public void receiveMessageFromGroupChat(Message message, Channel channel) {
        try {
            log.debug("接收到群组队列的消息message:{},channel:{}", message, channel);
            ChatMessageDTO chatMessageDTO = messageParseToChatMessageDTO(message);
            boolean exists = chatGroupMemberService.isMemberExists(chatMessageDTO.getToId(), chatMessageDTO.getFromId());
            if (!exists) {
                log.info("用户{}不在群组{}中，发送失败", chatMessageDTO.getFromId(), chatMessageDTO.getToId());
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setFromId(1L);
                chatMessage.setToId(Long.valueOf(chatMessageDTO.getFromId()));
                chatMessage.setMessageFormat(MessageFormat.TEXT);
                chatMessage.setMessageType(MessageType.NOTIFY);
                chatMessage.setCreateTime(LocalDateTime.now());
                chatMessage.setUpdateTime(LocalDateTime.now());
                chatMessage.setSourceType(SourceType.SYSTEM);
                ChatMessageContent content = new ChatMessageContent();
                content.setContent("您不在群组" + chatMessageDTO.getToId() + "中，发送失败");
                chatMessage.setChatMessageContent(content);
                webSocketService.sendMsgToUser(chatMessage, String.valueOf(chatMessageDTO.getFromId()));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            //获取发送方用户信息
            User user = userService.getById(chatMessageDTO.getFromId());
            //保存消息
            ChatMessage chatMessage = chatMessageService.saveChatMessage(user,chatMessageDTO);
            //更新聊天列表
            if (chatMessage != null) {
                chatListService.updateChatList(String.valueOf(chatMessage.getFromId()),String.valueOf( chatMessage.getToId()),chatMessage.getChatMessageContent(), SourceType.GROUP);
            }
            //发送消息
            if (null != chatMessage) {
                webSocketService.sendMsgToGroup(chatMessage, String.valueOf(chatMessage.getToId()));
            }
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error processing message", e);
            // 将消息标记为未确认并进入死信队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConstant.QUEUE_DEAD_GROUP_CHAT)
    public void receiveMessageDlqFromGroupChat(Message message, Channel channel) {
        ChatMessageDTO chatMessageDTO = messageParseToChatMessageDTO(message);

        // 处理消息（例如，保存到数据库）

        // 处理死信消息（例如，保存到数据库日志）
        log.info("receiveMessageDlq message: {}", chatMessageDTO);
        // 将处理结果通过 STOMP 广播到前端
//        messagingTemplate.convertAndSend("/topic/public", chatMessage);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConstant.QUEUE_TOPIC_BROAD_CAST)
    public void receiveMessageFromBroadChat(Message message, Channel channel) {
        try {
            ChatMessageDTO chatMessageDTO = messageParseToChatMessageDTO(message);
            //获取发送方用户信息
            User user = userService.getById(chatMessageDTO.getFromId());
            //保存消息
            ChatMessage chatMessage = chatMessageService.saveChatMessage(user,chatMessageDTO);
            //更新聊天列表
            if (chatMessage != null) {
                chatListService.updateChatList(String.valueOf(chatMessage.getFromId()),String.valueOf( chatMessage.getToId()),chatMessage.getChatMessageContent(), SourceType.SYSTEM);
            }
            //发送消息
            if (null != chatMessage) {
                webSocketService.sendNotifyAll(chatMessage);
            }
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error processing message", e);
            // 将消息标记为未确认并进入死信队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConstant.QUEUE_DEAD_GROUP_CHAT)
    public void receiveMessageDlqFromBroadChat(Message message, Channel channel) {
        ChatMessageDTO chatMessageDTO = messageParseToChatMessageDTO(message);

        // 处理消息（例如，保存到数据库）

        // 处理死信消息（例如，保存到数据库日志）
        log.info("receiveMessageDlq message: {}", chatMessageDTO);
        // 将处理结果通过 STOMP 广播到前端
//        messagingTemplate.convertAndSend("/topic/public", chatMessage);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 将序列化的消息反序列化为ChatMessageDTO消息对象
     * @param message
     * @return
     */
    private ChatMessageDTO messageParseToChatMessageDTO(Message message) {
        String jsonString = JSON.parseObject(message.getBody(), String.class);
       return JSON.parseObject(jsonString, ChatMessageDTO.class);
    }

}
