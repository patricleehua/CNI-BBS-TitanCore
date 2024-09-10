package com.titancore.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class Mail {
    /**
     * 邮件发送方
     */
    private String from;
    /**
     * 邮件接收方
     */
    private String to;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 抄送人(1/N)
     */
    private List<String> cc;
    /**
     * 附件地址
     */
    private String filePath;
    /**
     * 静态资源地址
     */
    private String rscPath;
    /**
     * 静态资源id
     */
    private String rscId;

}
