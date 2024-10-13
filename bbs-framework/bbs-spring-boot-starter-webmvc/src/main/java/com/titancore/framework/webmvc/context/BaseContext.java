package com.titancore.framework.webmvc.context;

/**
 * 记录线程信息
 */
@Deprecated(since = "后续改造成微服务有问题，无法保证单线程，弃用")
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
