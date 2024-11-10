package com.titancore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.ChatMessageRetraction;
import com.titancore.domain.mapper.ChatMessageRetractionMapper;
import com.titancore.service.ChatMessageRetractionService;
import org.springframework.stereotype.Service;

/**
* @author leehua
* @description 针对表【chat_message_retraction(消息撤回内容表)】的数据库操作Service实现
* @createDate 2024-11-08 10:57:12
*/
@Service
public class ChatMessageRetractionServiceImpl extends ServiceImpl<ChatMessageRetractionMapper, ChatMessageRetraction>
    implements ChatMessageRetractionService {

}




