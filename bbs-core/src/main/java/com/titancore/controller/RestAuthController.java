package com.titancore.controller;

import com.titancore.enums.AuthSourceType;
import com.titancore.properties.AuthProperties;
import com.xkcoding.http.config.HttpConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.scope.AuthGoogleScope;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import me.zhyd.oauth.utils.AuthScopeUtils;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * 第三方登入
 */
@RestController
@RequestMapping(value = "/auth")
@Slf4j
@Tag(name = "第三方登录")
public class RestAuthController {

    @Autowired
    private AuthProperties authProperties;

    @RequestMapping("/render/{source}")
    @Operation(description = "向三方接口申请认证链接")
    public void renderAuth(@PathVariable("source") String source,HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest(source);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    /**
     * 回调函数
     * @param source
     * @param callback
     * @return
     */
    @RequestMapping("/callback/{source}")
    @Operation(description = "三方登录成功后回调函数")
    public Object login(@PathVariable("source") String source, AuthCallback callback,HttpServletResponse response) throws IOException {
        log.info("callback start {}", callback);
        AuthRequest authRequest = getAuthRequest(source);
        AuthResponse<AuthUser> authResponse = authRequest.login(callback);
        if (authResponse.ok()){
            AuthUser data = authResponse.getData();
            //判断数据库是否有该用户

            //保存进数据库

            // 构造重定向 URL，包含生成的 Token
            String redirectUrl = "https://baidu.com/auth/callback?token=" ;
            response.sendRedirect(redirectUrl);
            return null;
        } else{
            //重定向？
            String redirectUrl = "https://error" ;
            response.sendRedirect(redirectUrl);
        }
        return null;
    }

    private AuthRequest getAuthRequest(String source) {
        AuthRequest authRequest = null;
        AuthSourceType authSourceType = AuthSourceType.valueOfAll(source.toLowerCase());
        if (authSourceType != null ){
            switch (authSourceType){
                case GITEE ->
                    authRequest = new AuthGiteeRequest(AuthConfig.builder()
                            .clientId(authProperties.getGitee().getClientId())
                            .clientSecret(authProperties.getGitee().getClientSecret())
                            .redirectUri(authProperties.getGitee().getRedirectUri())
                            .build());
                case GOOGLE ->
                    authRequest = new AuthGoogleRequest(AuthConfig.builder()
                            .clientId(authProperties.getGoogle().getClientId())
                            .clientSecret(authProperties.getGoogle().getClientSecret())
                            .redirectUri(authProperties.getGoogle().getRedirectUri())
                            .scopes(AuthScopeUtils.getScopes(AuthGoogleScope.USER_EMAIL, AuthGoogleScope.USER_PROFILE, AuthGoogleScope.USER_OPENID))
                            // 针对国外平台配置代理
                            .httpConfig(HttpConfig.builder()
                                    .timeout(authProperties.getProxy().getTimeout())
                                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(authProperties.getProxy().getHost(), authProperties.getProxy().getPort())))
                                    .build())
                            .build());
                case WECHAT_OPEN ->
                    authRequest = new AuthWeChatOpenRequest(AuthConfig.builder()
                            .clientId(authProperties.getWechatOpen().getClientId())
                            .clientSecret(authProperties.getWechatOpen().getClientSecret())
                            .redirectUri(authProperties.getWechatOpen().getRedirectUri())
                            .build());
            }
        }
        return authRequest;
    }

}
