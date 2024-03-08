package com.cmcorg20230301.be.engine.util.util;

import java.io.InputStream;
import java.util.function.Supplier;

import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 重试工具类
 */
@Slf4j
public class RetryUtil {

    /**
     * 执行：http请求
     */
    public static InputStream execHttpRequestInputStream(HttpRequest httpRequest) {

        // 重试 10次
        return execHttpRequestInputStream(httpRequest, 10);

    }

    /**
     * 执行：http请求
     *
     * @param execNumber 传值多少，则方法会执行多少次
     */
    public static InputStream execHttpRequestInputStream(HttpRequest httpRequest, int execNumber) {

        // 执行
        return execSupplier(() -> httpRequest.execute().bodyStream(), execNumber);

    }

    /**
     * 执行：http请求
     */
    public static String execHttpRequest(HttpRequest httpRequest) {

        // 重试 10次
        return execHttpRequest(httpRequest, 10);

    }

    /**
     * 执行：http请求
     *
     * @param execNumber 传值多少，则方法会执行多少次
     */
    public static String execHttpRequest(HttpRequest httpRequest, int execNumber) {

        // 执行
        return execSupplier(() -> httpRequest.execute().body(), execNumber);

    }

    /**
     * 执行
     *
     * @param execNumber 传值多少，则方法会执行多少次
     */
    public static <T> T execSupplier(Supplier<T> supplier, int execNumber) {

        return execSupplier(supplier, 0, execNumber);

    }

    /**
     * 执行
     *
     * @param gapMs 间隔多少毫秒
     * @param execNumber 传值多少，则方法会执行多少次
     */
    @SneakyThrows
    public static <T> T execSupplier(Supplier<T> supplier, long gapMs, long execNumber) {

        T result;

        try {

            result = supplier.get();

        } catch (Throwable e) {

            log.info("重试：{}", execNumber);

            if (execNumber == 1) {
                throw e;
            }

            if (gapMs > 0) {
                ThreadUtil.sleep(gapMs); // 睡眠
            }

            // 执行
            return execSupplier(supplier, gapMs, execNumber - 1);

        }

        return result;

    }

    /**
     * 执行
     *
     * @param gapMs 间隔多少毫秒
     * @param execNumber 传值多少，则方法会执行多少次
     */
    @SneakyThrows
    public static void execVoidFunc0(VoidFunc0 voidFunc0, long gapMs, long execNumber) {

        try {

            voidFunc0.call();

        } catch (Throwable e) {

            log.info("重试：{}", execNumber);

            if (execNumber == 1) {
                throw e;
            }

            if (gapMs > 0) {
                ThreadUtil.sleep(gapMs); // 睡眠
            }

            // 执行
            execVoidFunc0(voidFunc0, gapMs, execNumber - 1);

        }

    }

}
