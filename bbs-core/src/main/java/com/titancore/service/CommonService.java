package com.titancore.service;


import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.alibaba.fastjson.JSON;
import com.titancore.domain.dto.CaptchaCodeDTO;
import com.titancore.domain.entity.Mail;
import com.titancore.domain.entity.PostMediaUrl;
import com.titancore.enums.CapchaEnum;
import com.titancore.enums.LinkType;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.cloud.manager.config.CloudServiceFactory;
import com.titancore.constant.CloudStorePath;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.cloud.manager.domain.vo.FileListVo;
import com.titancore.framework.cloud.manager.urils.FileUtil;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.response.Response;
import com.titancore.framework.common.util.RegexUtils;
import com.titancore.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CommonService {
    @Resource
    private CloudServiceFactory factory;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PostMediaUrlService postMediaUrlService;
    @Value("${titan.cloud.maxSize}")
    private int maxSize;
    /**
     * 通用验证码发送方法，支持短信/邮箱
     * @param captchaCodeDTO 包含验证码相关信息的DTO
     * @return 处理结果
     */
    public Response<?> sendCaptchaCode(CaptchaCodeDTO captchaCodeDTO) {
        try {
            // 获取验证码类型
            CapchaEnum capchaType = CapchaEnum.valueOfAll(captchaCodeDTO.getCaptchaType());
            if (capchaType == null) {
                return Response.fail(ResponseCodeEnum.CAPTCHACODE_IS_ERROR);
            }

            // 获取是否为注册场景
            Integer isRegister = captchaCodeDTO.getIsRegister();

            // 生成验证码
            String code = generateRandomCode();

            return switch (capchaType) {
                case CAPTCHA_TYPE_PHONE -> handlePhoneCaptcha(captchaCodeDTO.getPhoneNumber(), code, isRegister);
                case CAPTCHA_TYPE_EMAIL -> handleEmailCaptcha(captchaCodeDTO.getEmailNumber(), code, isRegister);
            };
        } catch (Exception e) {
            log.error("处理验证码失败", e);
            return Response.fail((BizException) e);
        }
    }

    /**
     * 生成6位随机验证码
     * @return 随机验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(random.nextInt(10));
        }
        return randomNumber.toString();
    }

    /**
     * 处理手机验证码发送
     * @param phoneNumber 手机号码
     * @param code 验证码
     * @param isRegister 是否为注册场景
     * @return 处理结果
     */
    private Response<?> handlePhoneCaptcha(String phoneNumber, String code, Integer isRegister) {
        if (RegexUtils.isPhoneInvalid(phoneNumber)) {
            throw new BizException(ResponseCodeEnum.PHONE_NUMBER_IS_ERROR);
        }

        String redisKey = (isRegister == 0 ? RedisConstant.REGISTER_PHONE_PRIX : RedisConstant.PHONE_PRIX) + phoneNumber;

        if (isCaptchaAlreadySent(redisKey)) {
            return Response.success(getCaptchaRemainingTime(redisKey));
        }

        storeCaptchaInRedis(redisKey, code, RedisConstant.PHONE_TTL);
        String res = sendSms(phoneNumber, code);
        if ("OK".equals(res)) {
            log.info("手机验证码{}发送成功", code);
            return Response.success(isRegister == 0 ? CommonConstant.CaptchaCode_SEND_SUCCESS : CommonConstant.PHONE_SEND_SUCCESS);
        } else {
            throw new BizException(ResponseCodeEnum.CAPTCHACODE_IS_ERROR);
        }
    }

    /**
     * 处理邮箱验证码发送
     * @param emailNumber 邮箱地址
     * @param code 验证码
     * @param isRegister 是否为注册场景
     * @return 处理结果
     */
    private Response<?> handleEmailCaptcha(String emailNumber, String code, Integer isRegister) {
        if (RegexUtils.isEmailInvalid(emailNumber)) {
            throw new BizException(ResponseCodeEnum.EMAIL_NUMBER_IS_ERROR);
        }

        String redisKey = (isRegister == 0 ? RedisConstant.REGISTER_EMAIL_PRIX : RedisConstant.EMAIL_PRIX) + emailNumber;

        if (isCaptchaAlreadySent(redisKey)) {
            return Response.success(getCaptchaRemainingTime(redisKey));
        }

        Mail mail = new Mail();
        mail.setTo(emailNumber);
        String htmlContent = "<p>CNI国际论坛</p><span>你的验证码为:</span>" + code
                + "<span style='font-size:900'>5分钟内有效!</span>";
        mail.setContent(htmlContent);
        mail.setSubject("CNI-TITAN国际论坛");

        String res = emailService.sendHtmlMail(mail);
        if(res.equals(CommonConstant.EMAIL_SEND_SUCCESS)){
            storeCaptchaInRedis(redisKey, code, RedisConstant.EMAIL_TTL);
            return Response.success(CommonConstant.EMAIL_SEND_SUCCESS);
        }else{
            throw new BizException(ResponseCodeEnum.CAPTCHACODE_IS_ERROR);
        }
    }

    /**
     * 检查验证码是否已经发送
     * @param redisKey Redis键
     * @return 是否已发送
     */
    private boolean isCaptchaAlreadySent(String redisKey) {
        String s = stringRedisTemplate.opsForValue().get(redisKey);
        return StringUtils.isNotBlank(s);
    }

    /**
     * 获取验证码剩余时间
     * @param redisKey Redis键
     * @return 剩余时间
     */
    private Map<String, Long> getCaptchaRemainingTime(String redisKey) {
        Long lastTime = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        log.info("验证码{}已经存在, 剩余时间{}", redisKey, lastTime);
        Map<String, Long> data = new HashMap<>();
        data.put(CommonConstant.PHONE_ALREADY_SEND, lastTime);
        return data;
    }

    /**
     * 将验证码存入Redis
     * @param redisKey Redis键
     * @param code 验证码
     * @param ttl 有效时间
     */
    private void storeCaptchaInRedis(String redisKey, String code, long ttl) {
        stringRedisTemplate.opsForValue().set(redisKey, code, ttl, TimeUnit.MINUTES);
    }

    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @param code 验证码
     * @return 发送结果
     */
    private String sendSms(String phoneNumber, String code) {
        var cloudStorageService = factory.createService("aliyun");
        return cloudStorageService.sendMessage(phoneNumber, code);
    }

    /**
     * 文件下载
     * @param fileDownloadDTO
     * @return
     */
    @Operation
    public Map<String, Object> downloadFile(FileDownloadDTO fileDownloadDTO){
        var cloudStorageService = factory.createService();
        return cloudStorageService.exportOssFileInputStream(fileDownloadDTO);

    }

    /**
     * 文件上传
     * @param file
     * @param userId
     * @return
     */
    public String uploadFile(MultipartFile file,String userId){
        long size = file.getSize();
        AuthenticationUtil.checkUserId(userId);
        var cloudStorageService = factory.createService();
        String folderName = CloudStorePath.BASE_PATH
                + userId
                + "/" + CloudStorePath.FOLDER_PATH;
        return cloudStorageService.uploadFile(file,folderName,true);
    }

    /**
     * 生成文件临时Url(供下载使用)
     * @param fileDownloadDTO
     * @return
     */
    public String createTemporaryUrl(FileDownloadDTO fileDownloadDTO) {
        var cloudStorageService = factory.createService();
        String folderName = CloudStorePath.BASE_PATH
                + "/" + fileDownloadDTO.getUserId()
                + "/" + CloudStorePath.FOLDER_PATH;
        String filePath = folderName +"/"+ fileDownloadDTO.getFileName();
        return cloudStorageService.createTemplateUrlOfFile(filePath,fileDownloadDTO.getExpiresIn(),fileDownloadDTO.getIsPrivate().equals("0"));
    }

    /**
     * 上传 封面/背景/头像/未知
     * @param file
     * @param userId
     * @param type
     * @return
     */
    public String uploadMedia(MultipartFile file, String userId, String type) {
        //校验用户
        AuthenticationUtil.checkUserId(userId);
        //校验媒体格式
        LinkType linkType = LinkType.valueOfAll(type);
        if (linkType!= null){
            switch (linkType) {
                case AVATAR,BACKGROUND -> {
                    if (!FileUtil.isImage(file)) {
                        throw new BizException(ResponseCodeEnum.FILE_IS_NOT_IMAGE);
                    }
                }
                case VIDEO ->{
                    if (!FileUtil.isVideo(file)) {
                        throw new BizException(ResponseCodeEnum.FILE_IS_NOT_VIDEO);
                    }
                }
                case COVER,TAG,CATEGORY -> {
                    if (!FileUtil.isVideo(file) && !FileUtil.isImage(file)) {
                        throw new BizException(ResponseCodeEnum.FILE_IS_NOT_SUPPORTED);
                    }
                }
                case UNKNOWN ->{}
                default -> throw new BizException(ResponseCodeEnum.FILE_IS_NOT_SUPPORTED);
            }
        }

        var cloudStorageService = factory.createService();
        String FOLDER_PATH = LinkType.UNKNOWN.getValue();
        // 临时文章ID
        String postsId = null;

        // 处理头像类型
        if (type.equals(LinkType.AVATAR.getValue())) {
            FOLDER_PATH = type;
        // 处理封面/背景/视频类型
        } else if (type.equals(LinkType.BACKGROUND.getValue()) || type.equals(LinkType.COVER.getValue()) || type.equals(LinkType.VIDEO.getValue())) {
            // 检查 Redis 中是否已有临时帖子ID
            postsId = stringRedisTemplate.opsForValue().get(RedisConstant.TEMPORARYPOSTID_PRIX + userId);
            FOLDER_PATH = type ;
            if (postsId == null) {
                // 生成新的临时帖子ID
                SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();
                postsId = String.valueOf(snowflakeGenerator.next());
                // 设置 Redis 中的临时帖子ID
                stringRedisTemplate.opsForValue().set(RedisConstant.TEMPORARYPOSTID_PRIX + userId, postsId, RedisConstant.TEMPORARYPOSTID_TTL, TimeUnit.DAYS);
                FOLDER_PATH = postsId + "/" + FOLDER_PATH;
            }else{
                FOLDER_PATH = postsId + "/" + FOLDER_PATH;
            }
        }
        String folderName = CloudStorePath.BASE_PATH + userId + "/" + FOLDER_PATH + "/";
        if(type.equals(LinkType.TAG.getValue()) || type.equals(LinkType.CATEGORY.getValue())){
            folderName = CloudStorePath.BASE_PATH + "/" + "common" + "/" + type +"/";
        }
        String url = cloudStorageService.uploadImage(file, folderName, false);

        if (type.equals(LinkType.BACKGROUND.getValue()) || type.equals(LinkType.COVER.getValue()) || type.equals(LinkType.VIDEO.getValue())) {
            PostMediaUrl postMediaUrl = new PostMediaUrl();
            postMediaUrl.setMediaUrl(url);
            postMediaUrl.setPostId(Long.valueOf(postsId));
            postMediaUrl.setMediaType(linkType);
            //保存进数据库
            boolean save = postMediaUrlService.save(postMediaUrl);

            // 获取列表大小并进行非空检查
            Long size = stringRedisTemplate.opsForList().size(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + postsId);
            // 如果列表不存在则设置过期时间
            if (size == null || size == 0) {
                stringRedisTemplate.opsForList().rightPush(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + postsId, JSON.toJSONString(postMediaUrl));
                stringRedisTemplate.expire(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + postsId, RedisConstant.TEMPORARYPOSTMEDIA_TTL, TimeUnit.HOURS);
            } else {
                stringRedisTemplate.opsForList().rightPush(RedisConstant.TEMPORARYPOSTMEDIA_PRIX + postsId, JSON.toJSONString(postMediaUrl));
            }
        }
        return url;
    }

    /**
     * 删除文件
     * @param fileDelDTO
     * @return
     */
    public boolean deleteFile(FileDelDTO fileDelDTO) {
        String userId = fileDelDTO.getUserId();
        AuthenticationUtil.checkUserId(userId);
        var cloudStorageService  = factory.createService();
        return cloudStorageService.deleteByPath(fileDelDTO, fileDelDTO.isPrivate());
    }

    /**
     * 根据用户id查询用户上传的文件
     * @param userId
     * @return
     */
    public FileListVo queryFileListByUserId(String userId) {
        var cloudStorageService  = factory.createService();
        String prefix = CloudStorePath.BASE_PATH + userId+ "/" +CloudStorePath.FOLDER_PATH;
        FileListVo fileListVo = cloudStorageService.queryFileList(prefix, false, true);
        fileListVo.setUserId(userId);
        return fileListVo;
    }

    /**
     * 上传用户聊天文件
     * @param file
     * @param userId
     * @param toId
     * @param msgId
     * @return
     */
    public String uploadFileForChat(MultipartFile file, String userId,String toId, String msgId) {
        var cloudStorageService  = factory.createService();
        String path = CloudStorePath.CHAT_USER_BASE_PATH
                + userId + "/"
                + CloudStorePath.CHAT_USER_TO_USER_PATH
                + toId + "/"
                + CloudStorePath.CHAT_USER_FILES_PATH
                + CloudStorePath.CHAT_USER_MESSAGES_PATH
                + msgId + "/";
        return cloudStorageService.uploadFile(file, path,true);
    }
    /**
     * 上传用户聊天媒体
     * @param file
     * @param userId
     * @param toId
     * @param msgId
     * @return
     */
    public String uploadMediaForChat(MultipartFile file, String userId, String toId, String msgId) {
        var cloudStorageService  = factory.createService();
        String path = CloudStorePath.CHAT_USER_BASE_PATH
                + userId + "/"
                + CloudStorePath.CHAT_USER_TO_USER_PATH
                + toId + "/"
                + CloudStorePath.CHAT_USER_MEDIAS_PATH
                + CloudStorePath.CHAT_USER_MESSAGES_PATH
                + msgId + "/";
        return cloudStorageService.uploadFile(file, path,true);
    }

    /**
     * 断点下载
     * @param filePath 文件路径(带域名/不带域名）
     * @param offset 开始位置
     * @param length 结束位置
     * @return
     */
    public InputStream getFileToInputStreamByFilePath(String filePath,long offset, long length) {
        var cloudStorageService  = factory.createService();
        return cloudStorageService.BreakPointDownload(filePath,offset,length,true);
    }

    /**
     * 根据url 生成临时链接
     * @param filePath 文件路径(带域名/不带域名）
     * @param expiresIn hours
     * @param isPrivate 是否私有
     * @return
     */
    public String createTemporaryUrl(String filePath,int expiresIn,boolean isPrivate) {
        var cloudStorageService = factory.createService();
        return cloudStorageService.createTemplateUrlOfFile(filePath,expiresIn,isPrivate);
    }
}

