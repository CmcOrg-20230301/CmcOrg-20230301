package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.kafka.util.KafkaUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyKeyValueSet;
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
    private static void update(@NotNull String key, @NotNull VoidFunc0 voidFunc0) {

        voidFunc0.call(); // 执行方法

        // 发送：本地缓存移除的 topic
        KafkaUtil.sendLocalCacheRemoveTopic(CollUtil.newHashSet(key));

    }

    /**
     * 统一的执行 update方法：针对往 map里面设置值
     */
    @SneakyThrows
    private static void update(@NotNull String key, @NotNull String secondKey, @NotNull Func0<?> func0) {

        if (StrUtil.isBlank(secondKey)) {
            throw new RuntimeException("操作失败：更新时，secondKey不能为空，请联系管理员");
        }

        Object callObject = func0.call(); // 执行方法，获取返回值

        NotEmptyKeyValueSet notEmptyKeyValueSet = new NotEmptyKeyValueSet();

        notEmptyKeyValueSet.setKey(key);

        notEmptyKeyValueSet
            .setKeyValueSet(CollUtil.newHashSet(new NotEmptyKeyValueSet.KeyValue(secondKey, callObject)));

        // 发送：本地缓存更新的 topic，针对往 map里面设置值
        KafkaUtil.sendLocalCacheUpdateMapTopic(notEmptyKeyValueSet);

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
     * 添加：一般类型的缓存到 map里
     */
    @SneakyThrows
    public static <T> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @NotNull String secondKey, @NotNull T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        put(key, secondKey, defaultResult, func0);

    }

    /**
     * 添加：一般类型的缓存到 map里
     */
    @SneakyThrows
    public static <T> void put(@NotNull String key, @NotNull String secondKey, @NotNull T defaultResult,
        @Nullable Func0<T> func0) {

        update(key, secondKey, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResult); // 检查并设置值

            redissonClient.getMap(key).putAsync(secondKey, value);

            CacheLocalUtil.put(key, value); // 添加本地缓存

            return value;

        });

    }

    /**
     * 添加：map的缓存
     */
    @SneakyThrows
    public static <T extends Map<?, ?>> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @NotNull T defaultResult, @Nullable Func0<T> func0) {

        put(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 添加：map的缓存
     */
    @SneakyThrows
    public static <T extends Map<?, ?>> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @NotNull T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        update(key, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResult); // 检查并设置值

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
        @NotNull T defaultResult, @Nullable Func0<T> func0) {

        put(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 添加：collection的缓存
     */
    @SneakyThrows
    public static <T extends Collection<?>> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @NotNull T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        update(key, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResult); // 检查并设置值

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
