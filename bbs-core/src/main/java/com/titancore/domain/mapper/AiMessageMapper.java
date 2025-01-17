package com.titancore.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.titancore.domain.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;

/**
* @author leehua
* @description 针对表【ai_message(聊天记录表)】的数据库操作Mapper
* @createDate 2024-12-24 14:24:36
* @Entity generator.domain.AiMessage
*/
@Mapper
public interface AiMessageMapper extends BaseMapper<AiMessage> {

}




