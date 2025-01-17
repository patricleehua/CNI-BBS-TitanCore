package com.titancore.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiMessageVo {

    /**
     * 主键
     */
    private String id;

    /**
     * 会话id
     */
    private String aiSessionId;

    /**
     * 创建者id
     */
    private String creatorId;

    /**
     * 消息类型(用户/ 助手/ 系统)
     */
    private String type;

    /**
     * 消息内容
     */
    private String textContent;

//    /**
//     * 媒体内容如图片链接、语音链接
//     */
//    private List<AiMessage.Media> medias;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editedTime;


}
