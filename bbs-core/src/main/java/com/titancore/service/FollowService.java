package com.titancore.service;

import com.titancore.domain.dto.FollowDTO;
import com.titancore.domain.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.param.FollowParam;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.vo.DMLVo;

import java.util.List;


public interface FollowService extends IService<Follow> {

    PageResult queryList(FollowParam followParam);

    DMLVo buildFollow(FollowDTO followDTO);

    DMLVo changeFollowStatus(FollowDTO followDTO);

    DMLVo changeBlockStatus(FollowDTO followDTO);

    DMLVo removeFollow(FollowDTO followDTO);

    List<Long> highFollowedTop10();

    int followNumCount(Long userId,boolean isfansCount);
}
