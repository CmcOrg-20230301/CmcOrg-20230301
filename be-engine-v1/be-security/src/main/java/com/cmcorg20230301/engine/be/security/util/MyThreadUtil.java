package com.cmcorg20230301.engine.be.security.util;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
public class MyThreadUtil {

    private static TaskExecutor taskExecutor;
    private static TaskScheduler taskScheduler;

    public MyThreadUtil(TaskExecutor taskExecutor, TaskScheduler taskScheduler) {

        MyThreadUtil.taskExecutor = taskExecutor;
        MyThreadUtil.taskScheduler = taskScheduler;

    }

    /**
     * 异步执行
     */
    public static void execute(Runnable task) {

        taskExecutor.execute(task);

    }

    /**
     * 提交任务调度请求
     *
     * @param task    待执行任务
     * @param trigger 使用 Trigger指定任务调度规则
     */
    public static ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {

        return taskScheduler.schedule(task, trigger);

    }

    /**
     * 提交任务调度请求
     * 注意任务只执行一次，使用 startTime指定其启动时间
     *
     * @param task      待执行任务
     * @param startTime 任务启动时间
     */
    public static ScheduledFuture<?> schedule(Runnable task, Date startTime) {

        return taskScheduler.schedule(task, startTime);

    }

    /**
     * 使用 fixedRate的方式提交任务调度请求
     * 任务首次启动时间由传入参数指定
     *
     * @param task      待执行的任务
     * @param startTime 任务启动时间
     * @param period    两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {

        return taskScheduler.scheduleAtFixedRate(task, startTime, period);

    }

    /**
     * 使用 fixedRate的方式提交任务调度请求
     * 任务首次启动时间未设置，任务池将会尽可能早的启动任务
     *
     * @param task   待执行任务
     * @param period 两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {

        return taskScheduler.scheduleAtFixedRate(task, period);

    }

    /**
     * 使用 fixedDelay的方式提交任务调度请求
     * 任务首次启动时间由传入参数指定
     *
     * @param task      待执行的任务
     * @param startTime 任务启动时间
     * @param period    两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long period) {

        return taskScheduler.scheduleWithFixedDelay(task, startTime, period);

    }

    /**
     * 使用 fixedDelay的方式提交任务调度请求
     * 任务首次启动时间未设置，任务池将会尽可能早的启动任务
     *
     * @param task   待执行任务
     * @param period 两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long period) {

        return taskScheduler.scheduleWithFixedDelay(task, period);

    }

}
