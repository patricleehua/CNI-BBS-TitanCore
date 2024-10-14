package com.titancore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.ChatNoticeDTO;
import com.titancore.domain.entity.ChatGroupNotice;
import com.titancore.domain.param.ChatGroupNoticeParam;
import com.titancore.domain.param.PageResult;

public interface ChatGroupNoticeService extends IService<ChatGroupNotice> {
    /**
     * 创建公告
     * @param chatNoticeDTO
     * @return
     */
    boolean createNotice(ChatNoticeDTO chatNoticeDTO);

    /**
     * 根据群号获取群下的所有公告
     * @param chatGroupNoticeParam
     * @return
     */
    PageResult noticeList(ChatGroupNoticeParam chatGroupNoticeParam);

    /**
     * 根据公告id删除公告
     * @param noticeId
     * @return
     */
    boolean deleteNotice(String noticeId);

    /**
     * 根据群组Id删除所有历史公告
     * @param groupId
     * @return
     */

    boolean deleteNoticeByGroupId(String groupId);
}
