package com.titancore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.constant.MessageContentType;
import com.titancore.domain.dto.ChatMessageDTO;
import com.titancore.domain.dto.ChatMessageFileDTO;
import com.titancore.domain.dto.ReeditDTO;
import com.titancore.domain.dto.RetractionDTO;
import com.titancore.domain.entity.*;
import com.titancore.domain.mapper.ChatMessageMapper;
import com.titancore.domain.param.ChatMessageParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.ChatMessageRetractionVo;
import com.titancore.domain.vo.DMLVo;
import com.titancore.enums.*;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.service.*;
import com.titancore.util.AuthenticationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private ChatGroupMemberService chatGroupMemberService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

    @Override
    public PageResult messageRecord(ChatMessageParam chatMessageParam) {

        Page<ChatMessage> page = new Page<>(chatMessageParam.getPageNo(), chatMessageParam.getPageSize());
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getFromId, chatMessageParam.getFromId())
                .eq(ChatMessage::getToId, chatMessageParam.getTargetId())
                .or()
                .eq(ChatMessage::getFromId, chatMessageParam.getTargetId())
                .eq(ChatMessage::getToId, chatMessageParam.getFromId())
                .or()
                .eq(ChatMessage::getSourceType, SourceType.GROUP)
                .eq(ChatMessage::getToId, chatMessageParam.getTargetId());

        if(chatMessageParam.getIsDesc().equals(String.valueOf(StatusEnum.DISABLED.getValue()))){
            queryWrapper.orderByDesc(ChatMessage::getCreateTime);
        }else {
            queryWrapper.orderByAsc(ChatMessage::getCreateTime);
        }


        Page<ChatMessage> chatMessagePage = chatMessageMapper.selectPage(page, queryWrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(chatMessagePage.getTotal());
        pageResult.setRecords(chatMessagePage.getRecords());
        return pageResult;
    }

    @Override
    public String sendFileOnMsgId(MultipartFile file, String userId, String msgId) {
        AuthenticationUtil.checkUserId(userId);
        ChatMessage chatMessage = this.getById(msgId);
        String toId = chatMessage.getToId().toString();
        ChatMessageContent fileMsgContent = chatMessage.getChatMessageContent();
        JSONObject fileInfo = JSON.parseObject(fileMsgContent.getContent());
        //文件校验
        if(!Objects.equals(file.getOriginalFilename(), fileInfo.getString("fileName")) || fileInfo.getLong("fileSize") != file.getSize()){
            throw new BizException(ResponseCodeEnum.FILE_IS_NOT_MATCH);
        }
        String fileUrl = commonService.uploadFileForChat(file, userId, toId, msgId);
        //更新原始数据
        fileInfo.put("filePath",fileUrl);
        fileMsgContent.setContent(fileInfo.toJSONString());
        chatMessage.setChatMessageContent(fileMsgContent);
        chatMessage.setUpdateTime(LocalDateTime.now());
        this.updateById(chatMessage);
        return fileUrl;
    }


    @Override
    public String sendMediaOnMsgId(MultipartFile file, String userId, String msgId) {
        AuthenticationUtil.checkUserId(userId);
        ChatMessage chatMessage = this.getById(msgId);
        String toId = chatMessage.getToId().toString();
        ChatMessageContent fileMsgContent = chatMessage.getChatMessageContent();
        JSONObject fileInfo = JSON.parseObject(fileMsgContent.getContent());
        //文件校验
        if(!Objects.equals(file.getOriginalFilename(), fileInfo.getString("fileName")) || fileInfo.getLong("fileSize") != file.getSize()){
            throw new BizException(ResponseCodeEnum.FILE_IS_NOT_MATCH);
        }
        String mediaUrl = commonService.uploadMediaForChat(file, userId, toId, msgId);
        //更新原始数据
        fileInfo.put("filePath",mediaUrl);
        fileMsgContent.setContent(fileInfo.toJSONString());
        chatMessage.setChatMessageContent(fileMsgContent);
        chatMessage.setUpdateTime(LocalDateTime.now());
        this.updateById(chatMessage);
        return mediaUrl;
    }

    @Override
    public InputStream getFileToInputStreamByFilePath(String filePath,long offset, long length) {
        return commonService.getFileToInputStreamByFilePath(filePath,offset,length);
    }

    @Override
    public String getMedia(String userId, String msgId) {
        ChatMessageContent fileMsgContent = getFileMsgContent(new ChatMessageFileDTO(userId, msgId));
        JSONObject fileInfo = JSON.parseObject(fileMsgContent.getContent());
        String fileName = fileInfo.getString("fileName");
        String filePath = fileInfo.getString("filePath");
        String temporaryUrl = stringRedisTemplate.opsForValue().get(RedisConstant.MESSAGE_MEDIA_TEMPORARY_URL+fileName);
        if (StringUtils.isBlank(temporaryUrl)) {
            int expiresIn = (int) (RedisConstant.MESSAGE_MEDIA_TEMPORARY_URL_TTL * 24);
            temporaryUrl = commonService.createTemporaryUrl(filePath, expiresIn, true);
            stringRedisTemplate.opsForValue().set(RedisConstant.MESSAGE_MEDIA_TEMPORARY_URL+fileName,temporaryUrl,expiresIn, TimeUnit.HOURS);
        }
        return temporaryUrl;
    }

    @Override
    public ChatMessageContent getFileMsgContent(ChatMessageFileDTO chatMessageFileDTO) {
        String userId = chatMessageFileDTO.getUserId();
        String msgId = chatMessageFileDTO.getMsgId();
        ChatMessage chatMessage = this.getById(msgId);
        if(null == chatMessage){
            throw new BizException(ResponseCodeEnum.CHAT_MESSAGE_CONTENT_IS_NOT_EXIST);
        }
        if(chatMessage.getFromId().equals(Long.valueOf(userId))
                || chatMessage.getToId().equals(Long.valueOf(userId))
                || chatGroupMemberService.isMemberExists(String.valueOf(chatMessage.getToId()), userId) ){
            return chatMessage.getChatMessageContent();
        }else{
            throw new BizException(ResponseCodeEnum.CHAT_MESSAGE_CONTENT_IS_NOT_EXIST);
        }
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
