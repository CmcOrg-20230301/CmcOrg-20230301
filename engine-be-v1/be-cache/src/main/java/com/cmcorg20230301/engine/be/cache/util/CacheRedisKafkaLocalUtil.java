package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.VoidFunc0;
import com.cmcorg20230301.engine.be.kafka.util.KafkaUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.engine.be.redisson.util.RedissonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * redis缓存本地化，通过 kafka实现
 */
@Component
@Slf4j(topic = LogTopicConstant.CACHE_REDIS_KAFKA_LOCAL)
public class CacheRedisKafkaLocalUtil {

    private static RedissonClient redissonClient;

    public CacheRedisKafkaLocalUtil(RedissonClient redissonClient) {

        CacheRedisKafkaLocalUtil.redissonClient = redissonClient;

    }

    /**
     * 统一的执行 update方法
     */
    @SneakyThrows
    private static void update(String key, @NotNull VoidFunc0 voidFunc0) {

        voidFunc0.call(); // 执行方法

        // 发送：本地缓存移除的 topic
        KafkaUtil.sendLocalCacheRemoveTopic(CollUtil.newHashSet(key));

    }

    /**
     * 添加：一般类型的缓存
     */
    public static <T> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @NotNull T defaultResult,
        @Nullable Func0<T> func0) {

        put(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 添加：一般类型的缓存
     */
    @SneakyThrows
    public static <T> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @NotNull T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        update(key, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResult); // 检查并设置值

            redissonClient.getBucket(key).set(value); // 添加 redis缓存

            CacheLocalUtil.put(key, value); // 添加本地缓存

        });

    }

    /**
     * 添加：map的缓存
     */
    @SneakyThrows
    public static <T extends Map<?, ?>> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @NotNull T defaultResultMap, @Nullable Func0<T> func0) {

        put(redisKeyEnum, null, defaultResultMap, func0);

    }

    /**
     * 添加：map的缓存
     */
    @SneakyThrows
    public static <T extends Map<?, ?>> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @NotNull T defaultResultMap, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        update(key, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResultMap); // 检查并设置值

            T finalValue = value;
            RedissonUtil.batch((batch) -> {

                batch.getMap(key).deleteAsync();
                batch.getMap(key).putAllAsync(finalValue);

            });

            CacheLocalUtil.put(key, value); // 添加本地缓存

        });

    }

    /**
     * 添加：collection的缓存
     */
    @SneakyThrows
    public static <T extends Collection<?>> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @NotNull T defaultResultCollection, @Nullable Func0<T> func0) {

        put(redisKeyEnum, null, defaultResultCollection, func0);

    }

    /**
     * 添加：collection的缓存
     */
    @SneakyThrows
    public static <T extends Collection<?>> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @NotNull T defaultResultCollection, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        update(key, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResultCollection); // 检查并设置值

            T finalValue = value;
            RedissonUtil.batch((batch) -> {

                batch.getList(key).deleteAsync();
                batch.getList(key).addAllAsync(finalValue);

            });

            CacheLocalUtil.put(key, value); // 添加本地缓存

        });

    }

    /**
     * 移除缓存
     */
    public static void remove(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        update(key, () -> {

            redissonClient.getBucket(key).delete(); // 移除：redis缓存

            CacheLocalUtil.remove(key); // 移除：本地缓存

        });

    }

}
