package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.domain.entity.PostMediaUrl;
import com.titancore.domain.mapper.PostMediaUrlMapper;
import com.titancore.domain.vo.PostMediaUrlVo;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.service.CommonService;
import com.titancore.service.PostMediaUrlService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostMediaUrlServiceImpl extends ServiceImpl<PostMediaUrlMapper, PostMediaUrl> implements PostMediaUrlService {

    private final PostMediaUrlMapper postMediaUrlMapper;
    private final CommonService commonService;

    @Override
    public List<PostMediaUrlVo> queryMediaUrlListByPostId(Long post_id) {
        //todo redis
        List<PostMediaUrl> postMediaUrls = postMediaUrlMapper.selectList(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, post_id));
        List<PostMediaUrlVo> postMediaUrlVos = postMediaUrls.stream().map(mediaUrl -> {
            PostMediaUrlVo postMediaUrlVo = new PostMediaUrlVo();
            BeanUtils.copyProperties(mediaUrl, postMediaUrlVo);
            postMediaUrlVo.setMediaType(mediaUrl.getMediaType().getValue());
            postMediaUrlVo.setId(String.valueOf(mediaUrl.getId()));
            return postMediaUrlVo;
        }).collect(Collectors.toList());
        return postMediaUrlVos;
    }

    @Override
    public boolean deleteMediaUrlByPostId(String postId) {
        List<PostMediaUrl> postMediaUrls = postMediaUrlMapper.selectList(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, postId));
        List<Boolean> deleteResult = postMediaUrls.stream().map(postMediaUrl -> {
            String mediaUrl = postMediaUrl.getMediaUrl();
            FileDelDTO fileDelDTO = commonService.urlToFileDelDTO(mediaUrl);
            return commonService.deleteFile(fileDelDTO);
        }).toList();
        return deleteResult.stream().allMatch(Boolean::booleanValue);
    }

    @Override
    public void deleteMediaUrlById(Long id) {
        postMediaUrlMapper.deleteById(id);
    }

    @Override
    public void deleteMediaUrlByUrl(String url) {
        postMediaUrlMapper.delete(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getMediaUrl,url));
    }

}




