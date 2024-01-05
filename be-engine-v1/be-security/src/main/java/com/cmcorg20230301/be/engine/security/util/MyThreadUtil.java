package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.lang.func.VoidFunc0;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;

@Component
public class MyThreadUtil {

    private static TaskExecutor taskExecutor;
    private static TaskScheduler taskScheduler;

    public MyThreadUtil(TaskExecutor myTaskExecutor, TaskScheduler myTaskScheduler) {

        MyThreadUtil.taskExecutor = myTaskExecutor;
        MyThreadUtil.taskScheduler = myTaskScheduler;

    }

    /**
     * 异步执行
     */
    public static void execute(Runnable runnable) {

        taskExecutor.execute(runnable);

    }

    /**
     * 异步执行
     */
    public static void execute(VoidFunc0 voidFunc0, @Nullable CountDownLatch countDownLatch) {

        execute(voidFunc0, countDownLatch, null, null);

    }

    /**
     * 异步执行
     */
    public static void execute(VoidFunc0 voidFunc0, @Nullable CountDownLatch countDownLatch,
                               @Nullable VoidFunc0 exceptionVoidFunc0, @Nullable VoidFunc0 finallyVoidFunc0) {

        execute(() -> {

            TryUtil.tryCatchFinally(voidFunc0, exceptionVoidFunc0, () -> {

                if (countDownLatch != null) {

                    countDownLatch.countDown();

                }

                TryUtil.execVoidFunc0(finallyVoidFunc0);

            });

        });

    }

    /**
     * 提交任务调度请求
     *
     * @param runnable 待执行任务
     * @param trigger  使用 Trigger指定任务调度规则
     */
    public static ScheduledFuture<?> schedule(Runnable runnable, Trigger trigger) {

        return taskScheduler.schedule(runnable, trigger);

    }

    /**
     * 提交任务调度请求
     * 注意任务只执行一次，使用 startTime指定其启动时间
     *
     * @param runnable  待执行任务
     * @param startTime 任务启动时间
     */
    public static ScheduledFuture<?> schedule(Runnable runnable, Instant startTime) {

        return taskScheduler.schedule(runnable, startTime);

    }

    /**
     * 提交任务调度请求
     * 注意任务只执行一次，使用 startTime指定其启动时间
     *
     * @param runnable  待执行任务
     * @param startTime 任务启动时间
     */
    public static ScheduledFuture<?> schedule(Runnable runnable, Date startTime) {

        return taskScheduler.schedule(runnable, startTime);

    }

    /**
     * 使用 fixedRate的方式提交任务调度请求
     * 任务首次启动时间由传入参数指定
     *
     * @param runnable  待执行的任务
     * @param startTime 任务启动时间
     * @param period    两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, Date startTime, long period) {

        return taskScheduler.scheduleAtFixedRate(runnable, startTime, period);

    }

    /**
     * 使用 fixedRate的方式提交任务调度请求
     * 任务首次启动时间未设置，任务池将会尽可能早的启动任务
     *
     * @param runnable 待执行任务
     * @param period   两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long period) {

        return taskScheduler.scheduleAtFixedRate(runnable, period);

    }

    /**
     * 使用 fixedDelay的方式提交任务调度请求
     * 任务首次启动时间由传入参数指定
     *
     * @param runnable  待执行的任务
     * @param startTime 任务启动时间
     * @param period    两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, Date startTime, long period) {

        return taskScheduler.scheduleWithFixedDelay(runnable, startTime, period);

    }

    /**
     * 使用 fixedDelay的方式提交任务调度请求
     * 任务首次启动时间未设置，任务池将会尽可能早的启动任务
     *
     * @param runnable 待执行任务
     * @param period   两次任务启动时间之间的间隔时间，默认单位是毫秒
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long period) {

        return taskScheduler.scheduleWithFixedDelay(runnable, period);

    }

}
