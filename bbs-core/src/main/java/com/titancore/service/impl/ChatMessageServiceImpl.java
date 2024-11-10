package com.titancore.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.constant.MessageContentType;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.dto.ReeditDTO;
import com.titancore.domain.dto.RetractionDTO;
import com.titancore.domain.entity.*;
import com.titancore.domain.mapper.ChatMessageMapper;
import com.titancore.domain.vo.ChatMessageRetractionVo;
import com.titancore.domain.vo.DMLVo;
import com.titancore.enums.LevelType;
import com.titancore.enums.MessageType;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.SourceType;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.*;
import com.titancore.util.AuthenticationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ChatMessageRetractionService chatMessageRetractionService;
    @Autowired
    private ChatListService chatListService;
    @Override
    public DMLVo sendMessage(ChatMessageDTO chatMessageDTO) {
        //todo 判断当前登入用户是否与 发送方一致
        // 异常处理
        SourceType sourceType = SourceType.valueOfAll(chatMessageDTO.getSource());
        ChatMessageDTO result = null;
        if(sourceType!=null){
            switch(sourceType) {
                case USER -> result = sendMessageToUser(chatMessageDTO);
                case GROUP -> result = sendMessageToGroup(chatMessageDTO);
                case SYSTEM -> result = sendMessageToSystem(chatMessageDTO);
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
    private ChatMessageDTO sendMessageToSystem(ChatMessageDTO chatMessageDTO) {
        //todo 不同消息类型 的处理
        if (chatMessageDTO != null) {
            try{
                rbmqProducerService.sendMsgToSystem(chatMessageDTO);
            } catch (Exception e) {
                User user = userService.getById(chatMessageDTO.getFromId());
                ChatMessage chatMessage = convertDtoToChatMessage(user, chatMessageDTO);
                webSocketService.sendMsgAll(chatMessage);
            }
        }
        return  chatMessageDTO;
    }



    private ChatMessageDTO sendMessageToGroup(ChatMessageDTO chatMessageDTO) {
        //todo 不同消息类型 的处理
        //发送消息
        if (chatMessageDTO != null) {
            try{
                rbmqProducerService.sendMsgToGroup(chatMessageDTO);
            }catch (Exception e){
                User user = userService.getById(chatMessageDTO.getFromId());
                ChatMessage chatMessage = convertDtoToChatMessage(user, chatMessageDTO);
                webSocketService.sendMsgToGroup(chatMessage, String.valueOf(chatMessage.getToId()));
            }
        }
        return  chatMessageDTO;
    }

    private ChatMessageDTO sendMessageToUser(ChatMessageDTO chatMessageDTO) {
        //todo 不同消息类型 的处理
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

    @Override
    public ChatMessageRetractionVo reeditMessage(ReeditDTO reeditDTO) {
        AuthenticationUtil.checkUserId(reeditDTO.getUserId());
        ChatMessageRetraction chatMessageRetraction = chatMessageRetractionService.getOne(new LambdaQueryWrapper<ChatMessageRetraction>().eq(ChatMessageRetraction::getMsgId, reeditDTO.getMsgId()));
        ChatMessageRetractionVo chatMessageRetractionVo = new ChatMessageRetractionVo();
        chatMessageRetractionVo.setId(String.valueOf(chatMessageRetraction.getId()));
        chatMessageRetractionVo.setMsgContent(chatMessageRetraction.getMsgContent());
        chatMessageRetractionVo.setMsgId(String.valueOf(chatMessageRetraction.getMsgId()));
        return chatMessageRetractionVo;
    }
    @Transactional
    @Override
    public ChatMessage retractionMessage(RetractionDTO retractionDTO) {
        String userId = retractionDTO.getUserId();
        AuthenticationUtil.checkUserId(retractionDTO.getUserId());
        ChatMessage chatMessage = this.getById(retractionDTO.getMsgId());
        if (null == chatMessage)
            throw new BizException(ResponseCodeEnum.CHAT_MESSAGE_CONTENT_IS_NOT_EXIST);
        ChatMessageContent chatMessageContent = chatMessage.getChatMessageContent();
        chatMessageContent.setExt(chatMessageContent.getType());
        //保存入消息撤回表
        if(MessageType.TEXT.getValue().equals(chatMessageContent.getType())){
            ChatMessageRetraction chatMessageRetraction = new ChatMessageRetraction();
            chatMessageRetraction.setMsgId(chatMessage.getId());
            chatMessageRetraction.setMsgContent(chatMessageContent);
            chatMessageRetractionService.save(chatMessageRetraction);
        }
        //更改原始消息
        chatMessageContent.setType(MessageContentType.Retraction);
        chatMessageContent.setContent("");
        chatMessage.setChatMessageContent(chatMessageContent);
        updateById(chatMessage);
        //更新聊天列表 自己
        ChatList fromIdChatList = chatListService.getChatListByFromIdAndToId(String.valueOf(chatMessage.getFromId()), String.valueOf(chatMessage.getToId()));
        fromIdChatList.setLastMsgContent(chatMessageContent);
        chatListService.updateById(fromIdChatList);
        //更新聊天列表 对方
        ChatList toIdchatList = chatListService.getChatListByFromIdAndToId(String.valueOf(chatMessage.getToId()), String.valueOf(chatMessage.getFromId()));
        if(null != toIdchatList){
            toIdchatList.setLastMsgContent(chatMessageContent);
            chatListService.updateById(toIdchatList);
            //推送消息给对方
            webSocketService.sendMsgToUser(chatMessage, String.valueOf(chatMessage.getToId()));
        }
        return chatMessage;
    }

    /**
     * ChatMessageDTO 对象转换为 ChatMessage 对象
     * @param user
     * @param chatMessageDTO
     * @return
     */
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
