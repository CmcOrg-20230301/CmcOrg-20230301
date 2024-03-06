package com.cmcorg20230301.be.engine.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyKeyValueSet;
import com.cmcorg20230301.be.engine.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

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
    private static void remove(@NotNull String key, @NotNull VoidFunc0 voidFunc0) {

        voidFunc0.call(); // 执行方法

        // 发送：本地缓存移除的 topic
        KafkaUtil.sendLocalCacheRemoveTopic(CollUtil.newHashSet(key));

    }

    /**
     * 统一的执行 update方法：针对往 map里面移除值
     */
    @SneakyThrows
    private static void removeSecondMap(@NotNull String key, @NotNull String secondKey,
        @NotNull VoidFunc0 voidFunc0) {

        if (StrUtil.isBlank(secondKey)) {
            throw new RuntimeException("操作失败：更新时，secondKey不能为空，请联系管理员");
        }

        voidFunc0.call(); // 执行方法

        NotEmptyKeyValueSet notEmptyKeyValueSet = new NotEmptyKeyValueSet();

        notEmptyKeyValueSet.setKey(key);

        notEmptyKeyValueSet.setKeyValueSet(
            CollUtil.newHashSet(new NotEmptyKeyValueSet.KeyValue(secondKey, null)));

        // 发送：本地缓存更新的 topic，针对往 map里面移除值
        KafkaUtil.sendLocalCacheRemoveMapTopic(notEmptyKeyValueSet);

    }

    /**
     * 统一的执行 update方法：针对往 map里面设置值
     */
    @SneakyThrows
    private static void updateSecondMap(@NotNull String key, @NotNull String secondKey,
        @NotNull Func0<?> func0) {

        if (StrUtil.isBlank(secondKey)) {
            throw new RuntimeException("操作失败：更新时，secondKey不能为空，请联系管理员");
        }

        Object callObject = func0.call(); // 执行方法，获取返回值

        NotEmptyKeyValueSet notEmptyKeyValueSet = new NotEmptyKeyValueSet();

        notEmptyKeyValueSet.setKey(key);

        notEmptyKeyValueSet
            .setKeyValueSet(
                CollUtil.newHashSet(new NotEmptyKeyValueSet.KeyValue(secondKey, callObject)));

        // 发送：本地缓存更新的 topic，针对往 map里面设置值
        KafkaUtil.sendLocalCacheUpdateMapTopic(notEmptyKeyValueSet);

    }

    /**
     * 添加：一般类型的缓存
     */
    public static <T> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable T defaultResult,
        @Nullable Func0<T> func0) {

        put(redisKeyEnum, null, defaultResult, -1, func0);

    }

    /**
     * 添加：一般类型的缓存
     *
     * @param timeToLive 存活时间，单位毫秒：-1 永久
     */
    public static <T> void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey,
        @Nullable T defaultResult, long timeToLive, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        put(key, defaultResult, timeToLive, func0);

    }

    /**
     * 添加：一般类型的缓存
     *
     * @param timeToLive 存活时间，单位毫秒：-1 永久
     */
    public static <T> void put(@NotNull String key, long timeToLive, @Nullable Func0<T> func0) {

        put(key, null, timeToLive, func0);

    }

    /**
     * 添加：一般类型的缓存
     *
     * @param timeToLive 存活时间，单位毫秒：-1 永久
     */
    public static <T> void put(@NotNull String key, @Nullable T defaultResult, long timeToLive,
        @Nullable Func0<T> func0) {

        if (StrUtil.isBlank(key)) {
            return;
        }

        remove(key, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResult); // 检查并设置值

            redissonClient.getBucket(key).set(value, Duration.ofMillis(timeToLive)); // 添加 redis缓存

            CacheLocalUtil.put(key, value, timeToLive); // 添加本地缓存

        });

    }

    /**
     * 添加：一般类型的缓存到 map里
     */
    @SneakyThrows
    public static <T> void putSecondMap(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey,
        @NotNull String secondKey, @Nullable T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        putSecondMap(key, secondKey, defaultResult, func0);

    }

    /**
     * 添加：一般类型的缓存到 map里
     */
    @SneakyThrows
    public static <T> void putSecondMap(@NotNull String key, @NotNull String secondKey,
        @Nullable T defaultResult,
        @Nullable Func0<T> func0) {

        updateSecondMap(key, secondKey, () -> {

            T value = null;

            if (func0 != null) {
                value = func0.call();
            }

            value = CacheHelper.checkAndReturnResult(value, defaultResult); // 检查并设置值

            redissonClient.getMap(key).put(secondKey, value);

            CacheLocalUtil.putSecondMap(key, secondKey, value); // 添加本地缓存

            return value;

        });

    }

    /**
     * 添加：map类型的缓存
     */
    @SneakyThrows
    public static <T extends Map<?, ?>> void putMap(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable T defaultResult, @Nullable Func0<T> func0) {

        putMap(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 添加：map类型的缓存
     */
    @SneakyThrows
    public static <T extends Map<?, ?>> void putMap(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @Nullable T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        remove(key, () -> {

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

            CacheLocalUtil.put(key, value, -1); // 添加本地缓存

        });

    }

    /**
     * 添加：collection类型的缓存
     */
    @SneakyThrows
    public static <T extends Collection<?>> void putCollection(
        @NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable T defaultResult, @Nullable Func0<T> func0) {

        putCollection(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 添加：collection类型的缓存
     */
    @SneakyThrows
    public static <T extends Collection<?>> void putCollection(
        @NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @Nullable T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        remove(key, () -> {

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

            CacheLocalUtil.put(key, value, -1); // 添加本地缓存

        });

    }

    /**
     * 移除缓存
     */
    public static void remove(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        remove(key, () -> {

            redissonClient.getBucket(key).delete(); // 移除：redis缓存

            CacheLocalUtil.remove(key); // 移除：本地缓存

        });

    }

    /**
     * 移除缓存，从 map里
     */
    public static void removeSecondMap(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey,
        @NotNull String secondKey) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        removeSecondMap(key, secondKey, () -> {

            redissonClient.getMap(key).remove(secondKey); // 移除：redis缓存

            CacheLocalUtil.removeSecondMap(key, secondKey); // 移除：本地缓存

        });

    }

}
