package com.cmcorg20230301.be.engine.security.exception;

import com.cmcorg20230301.be.engine.security.util.MyExceptionUtil;

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        MyExceptionUtil.printError(e);

    }

}
