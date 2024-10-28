package com.titancore.framework.webmvc.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册web层相关组件如：interceptor
 */
@AutoConfiguration
@Slf4j
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    /**
     * 解决跨域
     * 这只解决了浏览器对跨域请求的限制,还要在controller配置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("跨域配置 CORS...");
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 配置域名可以用allowedOriginPatterns
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("token","iv") // 添加此行以暴露 iv/token 响应头
                .maxAge(3600);
    }

    /**
     * 自定义拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册自定义拦截器...");
        // 注册路由拦截器，自定义认证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
            SaRouter.match("/**")
                    .notMatch("/user/open/**",
                            "/common/open/**",
                            "/post/open/**",
                            "/category/open/**",
                            "/tag/open/**",
                            "/ws/**",
                            "/test/**",
                            "/chatroom/**",
                            "/favicon.ico","/swagger-resources/**", "/webjars/**", "/v3/**", "/doc.html"
                    )
                    .check(r -> {
                        if (!"OPTIONS".equalsIgnoreCase(SaHolder.getRequest().getMethod())) {
                            // 仅对非OPTIONS请求进行登录校验
                            StpUtil.checkLogin();
                        }
                    });

            // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
//            SaRouter.match("/**")
//                    .notMatch("/user/login")
//                    .check(r ->{
//                        StpUtil.checkLogin();
//                        Object loginId = StpUtil.getLoginId();
//                        long userid = SaFoxUtil.getValueByType(loginId, long.class);
//                        BaseContext.setCurrentId(userid);
//                        BaseContext.getCurrentId();
//                    });

            // 角色校验 -- 拦截以 admin 开头的路由，必须具备 admin 角色或者 super-admin 角色才可以通过认证
//            SaRouter.match("/admin/**", r -> StpUtil.checkRoleOr("admin", "super-admin"));
//
//            // 权限校验 -- 不同模块校验不同权限
//            SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
//            SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//            SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//            SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
//            SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
//            SaRouter.match("/comment/**", r -> StpUtil.checkPermission("comment"));
//
//            // 甚至你可以随意的写一个打印语句
//            SaRouter.match("/**", r -> System.out.println("----啦啦啦----"));
//
//            // 连缀写法
//            SaRouter.match("/**").check(r -> System.out.println("----啦啦啦----"));
        }));

    }

}
