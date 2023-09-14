package com.cmcorg20230301.be.engine.util.util;

import cn.hutool.core.io.IORuntimeException;
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
        return execHttpRequest(httpRequest, 3);

    }

    /**
     * 执行：http请求
     *
     * @param retryNumber 传值多少，则方法会执行多少次
     */
    public static String execHttpRequest(HttpRequest httpRequest, int retryNumber) {

        String resultStr;

        try {

            resultStr = httpRequest.execute().body();

        } catch (IORuntimeException e) { // 发生 IO异常，比如：建立连接超时等

            log.info("execHttpRequest，重试：{}", retryNumber);

            if (retryNumber == 1) {
                throw e;
            }

            // 执行
            return execHttpRequest(httpRequest, retryNumber - 1);

        }

        return resultStr;

    }

    /**
     * 执行：http请求
     *
     * @param retryNumber 传值多少，则方法会执行多少次
     */
    public static <T> T execSupplier(Supplier<T> supplier, int retryNumber) {

        T result;

        try {

            result = supplier.get();

        } catch (Exception e) {

            log.info("execSupplier，重试：{}", retryNumber);

            if (retryNumber == 1) {
                throw e;
            }

            // 执行
            return execSupplier(supplier, retryNumber - 1);

        }

        return result;

    }

}
