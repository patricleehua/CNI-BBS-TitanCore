package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.AiSessionDTO;
import com.titancore.domain.entity.AiMessage;
import com.titancore.domain.entity.AiSession;
import com.titancore.domain.mapper.AiSessionMapper;
import com.titancore.domain.vo.AiSessionVo;
import com.titancore.service.AiMessageService;
import com.titancore.service.AiSessionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author leehua
* @description 针对表【ai_session(会话记录表)】的数据库操作Service实现
* @createDate 2024-12-24 14:33:16
*/
@Service
public class AiSessionServiceImpl extends ServiceImpl<AiSessionMapper, AiSession>
    implements AiSessionService {
    @Resource
    private AiMessageService aiMessageService;
    @Override
    public List<AiSessionVo> findSessionsByUserId(String userId) {
        List<AiSession> aiSessions = this.baseMapper.selectList(new LambdaQueryWrapper<AiSession>().eq(AiSession::getCreatorId, userId));
        return aiSessions.stream().map(this::covertAiSessionToVo).toList();
    }

    @Override
    public AiSessionVo findSessionBySessionId(String sessionId) {
        AiSession aiSession = this.baseMapper.selectById(sessionId);
        return covertAiSessionToVo(aiSession);
    }

    @Override
    public boolean createSession(AiSessionDTO aiSessionDto) {
        AiSession aiSession = new AiSession();
        aiSession.setCreatorId(Long.valueOf(aiSessionDto.getUserId()));
        aiSession.setEditorId(Long.valueOf(aiSessionDto.getUserId()));
        aiSession.setName(aiSessionDto.getName());
        int insert = this.baseMapper.insert(aiSession);
        return insert > 0;
    }
    private AiSessionVo covertAiSessionToVo(AiSession aiSession){
        AiSessionVo aiSessionVo = new AiSessionVo();
        BeanUtils.copyProperties(aiSession, aiSessionVo);
        aiSessionVo.setId(String.valueOf(aiSession.getId()));
        aiSessionVo.setCreatorId(String.valueOf(aiSession.getCreatorId()));
        long count = aiMessageService.count(new LambdaQueryWrapper<AiMessage>().eq(AiMessage::getAiSessionId, aiSession.getId()));
        aiSessionVo.setNum((int) count);
        return aiSessionVo;
    }
}




