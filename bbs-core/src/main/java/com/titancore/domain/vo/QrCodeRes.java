package com.titancore.domain.vo;

import lombok.Data;

@Data
public class QrCodeRes {
    /**
     * 获取的二维码ticket，凭借此 ticket 可以在有效时间内换取二维码。
     */
    private String ticket;

    /**
     * 通过ticket换取的二维码
     */
    private String ticketUrl;

    /**
     * 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）
     */
    private Integer expire_seconds;

    /**
     * 二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
     */
    private String url;

    /**
     * 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
     */
    private String sceneStr;
}
