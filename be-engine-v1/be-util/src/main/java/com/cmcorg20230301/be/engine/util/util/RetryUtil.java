package com.cmcorg20230301.be.engine.util.util;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 重试工具类
 */
@Slf4j
public class RetryUtil {

    /**
     * 执行：http请求
     */
    public static String execHttpRequest(HttpRequest httpRequest) {

        // 重试 3次
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

        T result;

        try {

            result = supplier.get();

        } catch (Exception e) {

            log.info("重试：{}", execNumber);

            if (execNumber == 1) {
                throw e;
            }

            // 执行
            return execSupplier(supplier, execNumber - 1);

        }

        return result;

    }

}
