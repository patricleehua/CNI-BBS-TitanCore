package com.titancore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titancore.constant.CloudStorePath;
import com.titancore.domain.entity.PostMediaUrl;
import com.titancore.domain.mapper.PostMediaUrlMapper;
import com.titancore.domain.vo.MediaUrlVo;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.service.CommonService;
import com.titancore.service.PostMediaUrlService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostMediaUrlServiceImpl extends ServiceImpl<PostMediaUrlMapper, PostMediaUrl> implements PostMediaUrlService {

    private final PostMediaUrlMapper postMediaUrlMapper;
    @Lazy
    private final CommonService commonService;

    @Override
    public List<MediaUrlVo> queryMediaUrlListByPostId(Long post_id) {
        //todo redis
        List<PostMediaUrl> postMediaUrls = postMediaUrlMapper.selectList(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, post_id));
        List<MediaUrlVo> mediaUrlVos = postMediaUrls.stream().map(mediaUrl -> {
            MediaUrlVo mediaUrlVo = new MediaUrlVo();
            BeanUtils.copyProperties(mediaUrl, mediaUrlVo);
            mediaUrlVo.setMediaType(String.valueOf(mediaUrl.getMediaType()));
            mediaUrlVo.setId(String.valueOf(mediaUrl.getId()));
            return mediaUrlVo;
        }).collect(Collectors.toList());
        return mediaUrlVos;
    }

    @Override
    public boolean deleteMediaUrlByPostId(String postId) {
        List<PostMediaUrl> postMediaUrls = postMediaUrlMapper.selectList(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, postId));
        List<Boolean> deleteResult = postMediaUrls.stream().map(postMediaUrl -> {
            FileDelDTO fileDelDTO = new FileDelDTO();
            String mediaUrl = postMediaUrl.getMediaUrl();
            fileDelDTO.setFileName(getFileName(mediaUrl));
            fileDelDTO.setUserId(getUserId(mediaUrl));
            fileDelDTO.setPath(getFilePath(mediaUrl));
            fileDelDTO.setPrivate(isPrivate(mediaUrl));
            return commonService.deleteFile(fileDelDTO);
        }).toList();
        return deleteResult.stream().allMatch(Boolean::booleanValue);
    }
    private String getFileName(String fullPath){
        return fullPath.substring(fullPath.lastIndexOf("/")+1);
    }

    /**
     * 如果你的路径格式不固定，使用正则表达式会更灵活且易于维护。
     * @param fullPath
     * @return
     */
    private String getUserId(String fullPath){
        String regex = CloudStorePath.BASE_PATH+"(.*?)/";     // 正则表达式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fullPath);
        if (matcher.find()) {
            return matcher.group(1); // 提取 user id
        }
        return "";
    }
    private String getFilePath(String mediaUrl) {
        int startIndex = mediaUrl.indexOf(CloudStorePath.BASE_PATH);
        if (startIndex == -1) {
            return "";
        }
        int endIndex = mediaUrl.lastIndexOf("/");
        if (endIndex == -1 || endIndex <= startIndex) {
            return "";
        }
        return mediaUrl.substring(startIndex, endIndex + 1);
    }
    private boolean isPrivate(String mediaUrl) {
        return mediaUrl.contains("private");
    }
}




