package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.lang.func.VoidFunc1;
import org.jetbrains.annotations.Nullable;

/**
 * try-catch 工具类
 */
public class TryUtil {

    /**
     * 执行：try-catch
     */
    public static void tryCatch(VoidFunc0 voidFunc0) {

        try {

            voidFunc0.call();

        } catch (Throwable e) {

            MyExceptionUtil.printError(e);

        }

    }

    /**
     * 执行：try-catch
     */
    public static void tryCatch(VoidFunc0 voidFunc0, @Nullable VoidFunc1<Throwable> exceptionVoidFunc1) {

        try {

            voidFunc0.call();

        } catch (Throwable e) {

            MyExceptionUtil.printError(e);

            execVoidFunc1(exceptionVoidFunc1, e);

        }

    }

    /**
     * 执行：try-catch-finally
     */
    public static void tryCatchFinally(VoidFunc0 voidFunc0, @Nullable VoidFunc0 finallyVoidFunc0) {

        try {

            voidFunc0.call();

        } catch (Throwable e) {

            MyExceptionUtil.printError(e);

        } finally {

            execVoidFunc0(finallyVoidFunc0);

        }

    }

    /**
     * 执行：try-catch-finally
     */
    public static void tryCatchFinally(VoidFunc0 voidFunc0, @Nullable VoidFunc1<Throwable> exceptionVoidFunc1,
        @Nullable VoidFunc0 finallyVoidFunc0) {

        try {

            voidFunc0.call();

        } catch (Throwable e) {

            MyExceptionUtil.printError(e);

            execVoidFunc1(exceptionVoidFunc1, e);

        } finally {

            execVoidFunc0(finallyVoidFunc0);

        }

    }

    /**
     * 执行：VoidFunc0
     */
    public static void execVoidFunc0(@Nullable VoidFunc0 voidFunc0) {

        if (voidFunc0 == null) {
            return;
        }

        try {

            voidFunc0.call();

        } catch (Throwable e) {

            MyExceptionUtil.printError(e);

        }

    }

    /**
     * 执行：VoidFunc0
     */
    public static <T> void execVoidFunc1(@Nullable VoidFunc1<T> voidFunc1, T t) {

        if (voidFunc1 == null) {
            return;
        }

        try {

            voidFunc1.call(t);

        } catch (Throwable e) {

            MyExceptionUtil.printError(e);

        }

    }

}
