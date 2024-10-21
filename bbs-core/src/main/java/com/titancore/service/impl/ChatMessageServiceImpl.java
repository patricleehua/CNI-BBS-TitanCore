package com.titancore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.entity.ChatMessage;
import com.titancore.domain.entity.ChatMessageContent;
import com.titancore.domain.entity.User;
import com.titancore.domain.mapper.ChatMessageMapper;
import com.titancore.domain.vo.DMLVo;
import com.titancore.enums.LevelType;
import com.titancore.enums.MessageType;
import com.titancore.enums.SourceType;
import com.titancore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RBMQProducerService rbmqProducerService;
    @Autowired
    private WebSocketService webSocketService;
    @Override
    public DMLVo sendMessage(ChatMessageDTO chatMessageDTO) {
        //todo 判断当前登入用户是否与 发送方一致
        // 异常处理
        SourceType sourceType = SourceType.valueOfAll(chatMessageDTO.getSource());
        ChatMessageDTO result = null;
        if(sourceType!=null){
            switch(sourceType) {
                case USER -> result = sendMessageToUser(chatMessageDTO);
//                case GROUP -> result = sendMessageToGroup(chatMessageDTO);
            }
        }
        DMLVo dmlVo = new DMLVo();

        if (result != null) {
//            dmlVo.setId(String.valueOf(result.getId()));
//            dmlVo.setMessage(result.getCreateTime().toString());
            dmlVo.setStatus(true);
        }else{
            dmlVo.setMessage("发送失败");
            dmlVo.setStatus(false);
        }
        return dmlVo;
    }
    @Autowired
    private ChatListService chatListService;
    private ChatMessage sendMessageToSystem(ChatMessageDTO chatMessageDTO) {
        //获取发送方用户信息
        User user = userService.getById(chatMessageDTO.getFromId());
        //保存消息
        ChatMessage chatMessage = saveChatMessage(user,chatMessageDTO);
        //更新聊天列表
        if (chatMessage != null) {
            chatListService.updateChatList(chatMessageDTO.getFromId(),chatMessageDTO.getToId(), chatMessage.getChatMessageContent(),SourceType.SYSTEM);
        }

        //发送消息
        if (null != chatMessage) {
            try {
//                mqProducerService.sendMsgToUser(chatMessage);
            } catch (Exception e) {
                // todo 发送消息
//                webSocketService.sendMsgToUser(message, message.getToId());
            }
        }
        return  chatMessage;
    }



    private ChatMessage sendMessageToGroup(ChatMessageDTO chatMessageDTO) {
        //获取发送方用户信息
        User user = userService.getById(chatMessageDTO.getFromId());
        //保存消息
        ChatMessage chatMessage = saveChatMessage(user,chatMessageDTO);
        //更新聊天列表
        if (chatMessage != null) {
            chatListService.updateChatListGroup(String.valueOf(chatMessage.getToId()), chatMessage.getChatMessageContent());
        }

        //发送消息
        if (null != chatMessage) {
            try {
//                mqProducerService.sendMsgToUser(chatMessage);
            } catch (Exception e) {
                //发送消息
//                webSocketService.sendMsgToUser(message, message.getToId());
            }
        }
        return  chatMessage;
    }

    private ChatMessageDTO sendMessageToUser(ChatMessageDTO chatMessageDTO) {

        //发送消息
        if (null != chatMessageDTO) {
            try {
                rbmqProducerService.sendMsgToUser(chatMessageDTO);
            } catch (Exception e) {
                //获取发送方用户信息
                User user = userService.getById(chatMessageDTO.getFromId());
                ChatMessage chatMessage = convertDtoToChatMessage(user, chatMessageDTO);
                //发送消息
                webSocketService.sendMsgToUser(chatMessage, String.valueOf(chatMessage.getToId()));
            }
        }
        return  chatMessageDTO;
    }

    @Override
    public ChatMessage saveChatMessage(User user, ChatMessageDTO chatMessageDTO) {
        // 先转换 ChatMessageDTO 到 ChatMessage
        ChatMessage chatMessage = convertDtoToChatMessage(user, chatMessageDTO);

        // 保存消息到数据库
        int result = chatMessageMapper.insert(chatMessage);
        return result > 0 ? chatMessage : null;
    }

    private ChatMessage convertDtoToChatMessage(User user, ChatMessageDTO chatMessageDTO) {
        String toUserId = chatMessageDTO.getToId();
        LevelType levelType = LevelType.valueOfAll(chatMessageDTO.getLevel());
        ChatMessageContent chatMessageContent = chatMessageDTO.getChatMessageContent();
        SourceType sourceType = SourceType.valueOfAll(chatMessageDTO.getSource());
        MessageType messageType = MessageType.valueOfAll(chatMessageContent.getType());

        // 获取上一条显示时间的消息
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromId, user.getUserId())
                .eq(ChatMessage::getToId, toUserId)
                .or()
                .eq(ChatMessage::getFromId, toUserId)
                .eq(ChatMessage::getToId, user.getUserId())
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT 1");
        ChatMessage previousMessage = chatMessageMapper.selectOne(queryWrapper);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFromId(user.getUserId());
        chatMessage.setToId(Long.valueOf(toUserId));
        chatMessage.setType(messageType);
        chatMessage.setLevelType(levelType);
        chatMessage.setCreateTime(LocalDateTime.now());
        chatMessage.setUpdateTime(LocalDateTime.now());
        chatMessage.setSourceType(sourceType);

        if (previousMessage == null) {
            chatMessage.setIsShowTime(true);
        } else {
            chatMessage.setIsShowTime(Duration.between(previousMessage.getUpdateTime(), LocalDateTime.now()).toMinutes() > 5);
        }

        if (MessageType.AUDIO.equals(messageType)
                || MessageType.IMAGE.equals(messageType)
                || MessageType.VIDEO.equals(messageType)) {
            //todo 存储到对象存储服务
            Type type = new TypeReference<ChatMessageContent>(){}.getType();
            ChatMessageContent content = JSON.parseObject(chatMessageContent.getContent(), type);

//            JSONObject content = JSONUtil.parseObj(chatMessageContent.getContent());
//            String name = (String) content.get("name");
//            String fileType = name.substring(name.lastIndexOf(".") + 1);
//            String fileName = user.getUserId() + "/" + toUserId + "/" + IdUtil.randomUUID() + "." + fileType;
//            content.set("fileName", fileName);
////            content.set("url", minioUtil.getUrl(fileName));
//            content.set("type", fileType);
//            chatMessageContent.setContent(content.toJSONString(0));
        }
        chatMessage.setChatMessageContent(chatMessageContent);
        return chatMessage;
    }

}
