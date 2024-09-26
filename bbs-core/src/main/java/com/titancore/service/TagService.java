package com.titancore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.titancore.domain.dto.TagDTO;
import com.titancore.domain.entity.Tag;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.TagParam;
import com.titancore.domain.vo.DDLVo;
import com.titancore.domain.vo.TagVo;

import java.util.List;

public interface TagService extends IService<Tag> {
    /**
     * 创建标签
     * @param tagDTO
     * @return
     */
    DDLVo createTag(TagDTO tagDTO);

    /**
     * 根据帖子Id查询所拥有的标签列表
     * @param id
     * @return
     */

    List<TagVo> queryTagListByPostId(Long id);

    /**
     * 分页查询所有的标签列表（标签名可选）
     * @param tagParam
     * @return
     */

    PageResult queryList(TagParam tagParam);
}
