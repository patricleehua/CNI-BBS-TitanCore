package com.titancore.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "titan.auth")
@Data
public class AuthProperties {

    private Gitee gitee;
    private Google google;
    private WechatOpen wechatOpen;

    private Proxy proxy;

    @Getter
    @Setter
    public static class Proxy{
        private String host;
        private int port;
        private int timeout;
    }

    @Getter
    @Setter
    public static class Gitee{
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
    @Getter
    @Setter
    public static class Google{
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
    @Getter
    @Setter
    public static class WechatOpen{
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
}
