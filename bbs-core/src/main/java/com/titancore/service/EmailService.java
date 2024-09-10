package com.titancore.service;

import cn.hutool.core.util.ArrayUtil;
import com.titancore.domain.entity.Mail;
import com.titancore.framework.common.constant.CommonConstant;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;

    /**
     * 发送文本邮件
     * @param mail 邮件对象，包含收件人地址、邮件主题、邮件内容、抄送地址
     *
     */
    public String sendSimpleMail(Mail mail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            String fromAddress = StringUtils.isNotBlank(mail.getFrom()) ? mail.getFrom() : username;
            message.setFrom(fromAddress);
            message.setTo(mail.getTo());
            message.setSubject(mail.getSubject());
            message.setText(mail.getContent());
            if (ArrayUtil.isNotEmpty(mail.getCc())) {
                message.setCc(mail.getCc().toString());
            }
            mailSender.send(message);
            return CommonConstant.EMAIL_SEND_SUCCESS;
        } catch (MailException e) {
            log.error("发送邮件失败,收件人:{}", mail.getTo(), e);
            return CommonConstant.EMAIL_SEND_ERROR;
        }
    }


    /**
     * 发送HTML邮件
     * @param mail 邮件对象，包含收件人地址、邮件主题、邮件内容、抄送地址
     *
     */
    public  String sendHtmlMail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
            String fromAddress = StringUtils.isNotBlank(mail.getFrom()) ? mail.getFrom() : username;
            helper.setFrom(fromAddress);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);
            if (ArrayUtil.isNotEmpty(mail.getCc())) {
                helper.setCc(mail.getCc().toString());
            }
            mailSender.send(message);
            return CommonConstant.EMAIL_SEND_SUCCESS;
        } catch (MessagingException e) {
            log.error("发送邮件失败,收件人:{}", mail.getTo(), e);
            return CommonConstant.EMAIL_SEND_ERROR;
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param mail 邮件对象，包含收件人地址、主题、内容、附件路径和抄送地址
     */
    public String sendAttachmentsMail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String fromAddress = StringUtils.isNotBlank(mail.getFrom()) ? mail.getFrom() : username;
            helper.setFrom(fromAddress);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);
            if (ArrayUtil.isNotEmpty(mail.getCc())) {
                helper.setCc(mail.getCc().toString());
            }

            if (mail.getFilePath() != null && !mail.getFilePath().isEmpty()) {
                FileSystemResource file = new FileSystemResource(new File(mail.getFilePath()));
                String fileName = new File(mail.getFilePath()).getName(); // 修正文件名提取方式
                helper.addAttachment(fileName, file);
            }
            mailSender.send(message);
            return CommonConstant.EMAIL_SEND_SUCCESS;
        } catch (MessagingException e) {
            log.error("发送邮件失败,收件人:{}", mail.getTo(), e);
            return CommonConstant.EMAIL_SEND_ERROR;
        }
    }
    /**
     * 发送正文中有静态资源的邮件
     *
     * @param mail 邮件对象，包含收件人地址、主题、内容、静态资源路径、静态资源ID和抄送地址
     */
    public String sendResourceMail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String fromAddress = StringUtils.isNotBlank(mail.getFrom()) ? mail.getFrom() : username;
            helper.setFrom(fromAddress);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);
            if (ArrayUtil.isNotEmpty(mail.getCc())) {
                helper.setCc(mail.getCc().toString());
            }
            if (mail.getRscPath() != null && !mail.getRscPath().isEmpty() && mail.getRscId() != null) {
                FileSystemResource res = new FileSystemResource(new File(mail.getRscPath()));
                helper.addInline(mail.getRscId(), res);
            }
            mailSender.send(message);
            return CommonConstant.EMAIL_SEND_SUCCESS;
        } catch (MessagingException e) {
            log.error("发送邮件失败,收件人:{}", mail.getTo(), e);
            return CommonConstant.EMAIL_SEND_ERROR;
        }
    }
}
