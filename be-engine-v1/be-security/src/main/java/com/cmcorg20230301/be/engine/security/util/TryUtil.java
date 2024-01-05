package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.lang.func.VoidFunc0;
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

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

        }

    }

    /**
     * 执行：try-catch
     */
    public static void tryCatch(VoidFunc0 voidFunc0, @Nullable VoidFunc0 exceptionVoidFunc0) {

        try {

            voidFunc0.call();

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

            execVoidFunc0(exceptionVoidFunc0);

        }

    }

    /**
     * 执行：try-catch-finally
     */
    public static void tryCatchFinally(VoidFunc0 voidFunc0, @Nullable VoidFunc0 finallyVoidFunc0) {

        try {

            voidFunc0.call();

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

        } finally {

            execVoidFunc0(finallyVoidFunc0);

        }

    }

    /**
     * 执行：try-catch-finally
     */
    public static void tryCatchFinally(VoidFunc0 voidFunc0, @Nullable VoidFunc0 exceptionVoidFunc0, @Nullable VoidFunc0 finallyVoidFunc0) {

        try {

            voidFunc0.call();

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

            execVoidFunc0(exceptionVoidFunc0);

        } finally {

            execVoidFunc0(finallyVoidFunc0);

        }

    }

    /**
     * 执行：VoidFunc0
     */
    private static void execVoidFunc0(@Nullable VoidFunc0 voidFunc0) {

        if (voidFunc0 == null) {
            return;
        }

        try {

            voidFunc0.call();

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

        }

    }

}
