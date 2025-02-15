package com.titancore.service;

import com.titancore.domain.dto.FollowDTO;
import com.titancore.domain.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.param.FollowParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.DMLVo;
import com.titancore.enums.FollowStatus;

import java.util.HashMap;
import java.util.List;


public interface FollowService extends IService<Follow> {

    PageResult queryList(FollowParam followParam);

    DMLVo buildFollow(FollowDTO followDTO);

    DMLVo changeFollowStatus(FollowDTO followDTO);

    DMLVo changeBlockStatus(FollowDTO followDTO);

    DMLVo removeFollow(FollowDTO followDTO);

    List<Long> highFollowedTop10();

    int followNumCount(Long userId,boolean isfansCount);

    /**
     * 查询关注数
     * @param userId
     * @return
     */
    HashMap<String, Long> queryFollowCount(String userId);

    /**
     * 检查用户对目标用户的关注状态
     * @param targetUserId
     * @param beFollowedUserId
     * @return
     */
    FollowStatus getUserFollowStatus(String targetUserId, String beFollowedUserId);
}
