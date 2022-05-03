package com.liner.reggie.common;

/**
 * 基于ThreadLocal 封装用户类
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal  = new ThreadLocal<>();

    public static void setThreadLocal(Long id) {
       threadLocal.set(id);
    }

    public static Long getThreadLocal() {
        return threadLocal.get();
    }
    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
