package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.AiSessionDTO;
import com.titancore.domain.entity.AiSession;
import com.titancore.domain.vo.AiSessionVo;

import java.util.List;

/**
* @author leehua
* @description 针对表【ai_session(会话记录表)】的数据库操作Service
* @createDate 2024-12-24 14:33:16
*/
public interface AiSessionService extends IService<AiSession> {

    List<AiSessionVo> findSessionsByUserId(String userId);

    AiSessionVo findSessionBySessionId(String sessionId);

    boolean createSession(AiSessionDTO aiSessionDto);
}
