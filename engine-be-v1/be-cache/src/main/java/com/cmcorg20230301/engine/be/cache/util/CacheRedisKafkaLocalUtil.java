package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.core.lang.func.Func0;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * redis缓存本地化，通过 kafka实现
 */
@Component
public class CacheRedisKafkaLocalUtil {

    private static RedissonClient redissonClient;

    public CacheRedisKafkaLocalUtil(RedissonClient redissonClient) {

        CacheRedisKafkaLocalUtil.redissonClient = redissonClient;

    }

    /**
     * 添加：一般类型的缓存
     */
    public static <T> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @NotNull T defaultResult,
        @NotNull Func0<T> func0) {

        put(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 添加：一般类型的缓存
     */
    @SneakyThrows
    public static <T> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @NotNull T defaultResult, @NotNull Func0<T> func0) {

        String key = CacheRedisUtil.getKey(redisKeyEnum, sufKey);

        T value = func0.call();

        value = CacheHelper.checkAndReturnResult(value, defaultResult); // 检查并设置值

        redissonClient.getBucket(key).set(value);

        CacheLocalUtil.put();

    }

    /**
     * 添加：map的缓存
     */
    public static void putMap() {

    }

    /**
     * 添加：collection的缓存
     */
    public static void putCollection() {

    }

    /**
     * 移除缓存
     */
    public static void remove() {

    }

    /**
     * 添加：一般类型的缓存
     */
    public static void get() {

    }

    /**
     * 添加：一般类型的缓存
     */
    public static void getMap() {

    }

    /**
     * 添加：一般类型的缓存
     */
    public static void getCollection() {

    }

}
