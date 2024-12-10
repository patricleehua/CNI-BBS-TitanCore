package com.titancore.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.dto.ChatNoticeDTO;
import com.titancore.domain.entity.ChatGroup;
import com.titancore.domain.entity.ChatGroupNotice;
import com.titancore.domain.mapper.ChatGroupMapper;
import com.titancore.domain.mapper.ChatGroupNoticeMapper;
import com.titancore.domain.param.ChatGroupNoticeParam;
import com.titancore.domain.param.PageResult;
import com.titancore.service.ChatGroupNoticeService;
import com.titancore.util.AuthenticationUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@AllArgsConstructor
public class ChatGroupNoticeServiceImpl extends ServiceImpl<ChatGroupNoticeMapper, ChatGroupNotice>
    implements ChatGroupNoticeService {

    private final ChatGroupNoticeMapper chatGroupNoticeMapper;
    private final ChatGroupMapper chatGroupMapper;
    @Override
    public boolean createNotice(ChatNoticeDTO chatNoticeDTO) {
        String userId = chatNoticeDTO.getOwnerUserId();
        AuthenticationUtil.checkUserId(userId);

        ChatGroupNotice chatGroupNotice = new ChatGroupNotice();
//        String jsonString = JSON.toJSONString(noticeVo);
//        Type tyep = new TypeReference<ChatGroupNotice>(){}.getType();
//        ChatGroupNotice chatGroupNotice  = JSON.parseObject(jsonString, tyep);

        int result = 0 ;
        if(StringUtils.isNotEmpty(chatNoticeDTO.getNoticeId())){
            chatGroupNotice.setChatGroupId(Long.valueOf(chatNoticeDTO.getGroupId()));
            if(StringUtils.isNotBlank(chatNoticeDTO.getNoticeContent())){
                chatGroupNotice.setNoticeContent(chatNoticeDTO.getNoticeContent());
            }
            chatGroupNotice.setId(Long.valueOf(chatNoticeDTO.getNoticeId()));
            chatGroupNotice.setUserId(Long.valueOf(userId));
            chatGroupNotice.setUpdateTime(new Date());
            result = chatGroupNoticeMapper.updateById(chatGroupNotice);
        }else{
            chatGroupNotice.setChatGroupId(Long.valueOf(chatNoticeDTO.getGroupId()));
            chatGroupNotice.setNoticeContent(chatNoticeDTO.getNoticeContent());
            chatGroupNotice.setUserId(Long.valueOf(userId));
            chatGroupNotice.setCreateTime(new Date());
            chatGroupNotice.setUpdateTime(new Date());
             result = chatGroupNoticeMapper.insert(chatGroupNotice);
        }
        int update = 0;
        if( result > 0 ){
            String jsonStr = JSON.toJSONString(chatGroupNotice);
            update = chatGroupMapper.update(new LambdaUpdateWrapper<ChatGroup>()
                    .set(ChatGroup::getNotice, jsonStr)
                    .eq(ChatGroup::getId, chatGroupNotice.getChatGroupId()));
        }
        return result > 0 && update >0;
    }

    @Override
    public PageResult noticeList(ChatGroupNoticeParam chatGroupNoticeParam) {
        Page<ChatGroupNotice> page = new Page<>(chatGroupNoticeParam.getPageNo(), chatGroupNoticeParam.getPageSize());
        LambdaQueryWrapper<ChatGroupNotice> wrapper = new LambdaQueryWrapper<ChatGroupNotice>();
        if (StringUtils.isNotBlank(chatGroupNoticeParam.getGroupId())) {
            wrapper.eq(ChatGroupNotice::getChatGroupId,chatGroupNoticeParam.getGroupId());
        }
        Page<ChatGroupNotice> chatGroupNoticePage = chatGroupNoticeMapper.selectPage(page, wrapper);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(chatGroupNoticePage.getTotal());
        pageResult.setRecords(chatGroupNoticePage.getRecords());
        return pageResult;
    }

    @Override
    public boolean deleteNotice(String  noticeId) {
        int result = chatGroupNoticeMapper.deleteById(noticeId);
        return result > 0;
    }

    @Override
    public boolean deleteNoticeByGroupId(String groupId) {
        int result = chatGroupNoticeMapper.delete(new LambdaQueryWrapper<ChatGroupNotice>().eq(ChatGroupNotice::getChatGroupId, groupId));
        return result>0;
    }
}




