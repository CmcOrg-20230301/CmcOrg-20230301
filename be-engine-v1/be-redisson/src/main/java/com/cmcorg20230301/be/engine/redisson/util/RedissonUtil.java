package com.cmcorg20230301.be.engine.redisson.util;

import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.lang.func.VoidFunc1;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Supplier;

@Component
public class RedissonUtil {

    private static final String PRE_REDISSON = "PRE_REDISSON:";

    private static RedissonClient redissonClient;

    public RedissonUtil(RedissonClient redissonClient) {
        RedissonUtil.redissonClient = redissonClient;
    }

    /**
     * 执行批量操作
     */
    @SneakyThrows
    public static void batch(@NotNull VoidFunc1<RBatch> voidFunc1) {

        RBatch batch = redissonClient.createBatch();

        voidFunc1.call(batch);

        batch.execute(); // 执行批量操作

    }

    /**
     * 获取一般的锁，并执行方法
     */
    @SneakyThrows
    public static void doLock(@NotNull String str, @NotNull VoidFunc0 voidFunc0) {

        RLock lock = redissonClient.getLock(PRE_REDISSON + str);

        lock.lock();

        try {

            voidFunc0.call();

        } finally {

            lock.unlock();

        }

    }

    /**
     * 获取一般的锁，并执行方法
     */
    public static <T> T doLock(@NotNull String str, @NotNull Supplier<T> supplier) {

        RLock lock = redissonClient.getLock(PRE_REDISSON + str);

        lock.lock();

        try {

            return supplier.get();

        } finally {

            lock.unlock();

        }

    }

    /**
     * 获取连锁，并执行方法
     */
    @SneakyThrows
    public static void doMultiLock(@Nullable String preName, Set<?> nameSet, @NotNull VoidFunc0 voidFunc0,
        RLock... lockArr) {

        RLock lock = getMultiLock(preName, nameSet, lockArr);

        lock.lock();

        try {

            voidFunc0.call();

        } finally {

            lock.unlock();

        }

    }

    /**
     * 获取连锁，并执行方法
     */
    public static <T> T doMultiLock(@Nullable String preName, Set<?> nameSet, @NotNull Supplier<T> supplier,
        RLock... lockArr) {

        RLock lock = getMultiLock(preName, nameSet, lockArr);

        lock.lock();

        try {

            return supplier.get();

        } finally {

            lock.unlock();

        }

    }

    /**
     * 获取连锁
     */
    private static RLock getMultiLock(@Nullable String preName, Set<?> nameSet, RLock... tempLockArr) {

        if (preName == null) {
            preName = ""; // 防止：null 变成 "null"
        }

        RLock[] lockArr;

        if (tempLockArr == null) {

            lockArr = new RLock[nameSet.size()];

        } else {

            lockArr = new RLock[nameSet.size() + tempLockArr.length];

        }

        int i = 0;

        for (Object item : nameSet) {

            lockArr[i] = redissonClient.getLock(PRE_REDISSON + preName + item); // 设置锁名

            i++;

        }

        if (tempLockArr != null) {

            for (RLock item : tempLockArr) {

                lockArr[i] = item;

                i++;

            }

        }

        return redissonClient.getMultiLock(lockArr);

    }

}
