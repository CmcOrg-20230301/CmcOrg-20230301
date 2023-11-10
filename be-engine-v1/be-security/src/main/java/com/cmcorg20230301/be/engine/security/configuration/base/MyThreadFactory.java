package com.cmcorg20230301.be.engine.security.configuration.base;

import com.cmcorg20230301.be.engine.security.exception.MyUncaughtExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {

    ThreadFactory threadFactory;

    public MyThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {

        Thread thread = threadFactory.newThread(r);

        thread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());

        return thread;

    }

}
