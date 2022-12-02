package com.ocs.common.task;

/**
 * 系统任务执行上线文
 *
 * @author tangyixiang
 * @Date 2022/11/9
 */
public class TaskContext {

    private static ThreadLocal<SysJobRuntime> threadLocal = new ThreadLocal<>();


    public static SysJobRuntime get() {
        return threadLocal.get();
    }

    public static void set(SysJobRuntime sysJobRuntime) {
        threadLocal.set(sysJobRuntime);
    }

    public static void clear() {
        threadLocal.remove();
    }

}
