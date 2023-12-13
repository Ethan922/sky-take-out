package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal1 = new ThreadLocal<>();
    public static ThreadLocal<Long> threadLocal2 = new ThreadLocal<>();

    public static void setCurrentUserId(Long id){
        threadLocal2.set(id);
    }
    public static void setCurrentEmpId(Long id) {
        threadLocal1.set(id);
    }

    public static Long getCurrentEmpId() {
        return threadLocal1.get();
    }

    public static Long getCurrentUserId() {
        return threadLocal2.get();
    }

    public static void removeCurrentEmpId() {
        threadLocal1.remove();
    }

    public static void removeCurrentUserId() {
        threadLocal2.remove();
    }

}
