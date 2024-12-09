package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.PostMediaUrl;
import com.titancore.domain.mapper.MediaUrlMapper;
import com.titancore.domain.vo.MediaUrlVo;
import com.titancore.service.MediaUrlService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaUrlServiceImpl extends ServiceImpl<MediaUrlMapper, PostMediaUrl> implements MediaUrlService {

    @Autowired
    private MediaUrlMapper mediaUrlMapper;
    @Override
    public List<MediaUrlVo> queryMediaUrlListByPostId(Long post_id) {
        //todo redis
        List<PostMediaUrl> postMediaUrls = mediaUrlMapper.selectList(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, post_id));
        List<MediaUrlVo> mediaUrlVos = postMediaUrls.stream().map(mediaUrl -> {
            MediaUrlVo mediaUrlVo = new MediaUrlVo();
            BeanUtils.copyProperties(mediaUrl, mediaUrlVo);
            mediaUrlVo.setMediaType(String.valueOf(mediaUrl.getMediaType()));
            mediaUrlVo.setId(String.valueOf(mediaUrl.getId()));
            return mediaUrlVo;
        }).collect(Collectors.toList());
        return mediaUrlVos;
    }
}




