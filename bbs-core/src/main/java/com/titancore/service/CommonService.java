package com.titancore.service;


import com.titancore.domain.dto.CaptchaCodeDTO;
import com.titancore.domain.entity.Mail;
import com.titancore.enums.CapchaEnum;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.framework.cloud.manager.config.CloudServiceFactory;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.common.constant.CommonConstant;
import com.titancore.framework.common.constant.RedisConstant;
import com.titancore.framework.common.exception.BizException;
import com.titancore.framework.common.response.Response;
import com.titancore.framework.common.util.RegexUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
                return Response.fail(ResponseCodeEnum.CAPTCHACODE_ISERROR);
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
            throw new BizException(ResponseCodeEnum.PHONE_NUMBER_ISERROR);
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
            throw new BizException(ResponseCodeEnum.CAPTCHACODE_ISERROR);
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
            throw new BizException(ResponseCodeEnum.EMAIL_NUMBER_ISERROR);
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
            throw new BizException(ResponseCodeEnum.CAPTCHACODE_ISERROR);
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
        var cloudStorageService = factory.createService();
        return cloudStorageService.sendMessage(phoneNumber, code);
    }

    /**
     * 文件下载
     * @param fileDownloadDTO
     * @return
     */
    public Map<String, Object> downloadFile(FileDownloadDTO fileDownloadDTO){
        var cloudStorageService = factory.createService();
        return cloudStorageService.exportOssFileInputStream(fileDownloadDTO);

    }


}

