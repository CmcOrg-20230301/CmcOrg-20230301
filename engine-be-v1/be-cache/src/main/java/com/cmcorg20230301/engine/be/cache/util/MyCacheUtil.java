package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.engine.be.redisson.util.RedissonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 * 获取缓存逻辑：先从 本地获取缓存，再从 redis获取缓存，最后从 数据提供者获取数据
 */
@Component
@Slf4j(topic = LogTopicConstant.CACHE)
public class MyCacheUtil {

    private static RedissonClient redissonClient;

    public MyCacheUtil(RedissonClient redissonClient) {

        MyCacheUtil.redissonClient = redissonClient;

    }

    /**
     * 获取：一般类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T> T get(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable T defaultResult,
        @Nullable Func0<T> func0) {

        return get(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 获取：一般类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T> T get(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @Nullable T defaultResult, @Nullable Func0<T> func0) {

        return get(redisKeyEnum, sufKey, defaultResult, -1, func0);

    }

    /**
     * 获取：一般类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T> T get(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @Nullable T defaultResult, long timeToLive, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        T result = onlyGet(key, false);

        if (result != null) {
            return result;
        }

        if (func0 != null) {

            log.info("{}：读取提供者的数据", key);
            result = func0.call();

        }

        result = CacheHelper.checkAndReturnResult(result, defaultResult); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        if (timeToLive < 1) {
            redissonClient.<T>getBucket(key).set(result); // 先加入到 redis里
        } else {
            redissonClient.<T>getBucket(key).set(result, timeToLive, TimeUnit.MILLISECONDS); // 先加入到 redis里
        }

        log.info("{}：加入 本地缓存", key);
        CacheLocalUtil.put(key, result, timeToLive);

        return result;

    }

    /**
     * 只获取值
     */
    @SneakyThrows
    @Nullable
    public static <T> T onlyGet(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        boolean getRemainTimeFlag) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        return onlyGet(key, getRemainTimeFlag);

    }

    /**
     * 只获取值
     *
     * @param getRemainTimeFlag 是否获取 redis的过期时间
     */
    @SneakyThrows
    @Nullable
    public static <T> T onlyGet(@NotNull String key, boolean getRemainTimeFlag) {

        T result = CacheLocalUtil.get(key);

        if (result != null) {

            log.info("{}：返回 本地缓存", key);
            return result;

        }

        RBucket<T> bucket = redissonClient.getBucket(key);

        result = bucket.get();

        if (result == null) {

            return null;

        }

        log.info("{}：加入 本地缓存，并返回 redis缓存", key);

        long remainTimeToLive;

        if (getRemainTimeFlag) {
            remainTimeToLive = bucket.remainTimeToLive();
        } else {
            remainTimeToLive = -1;
        }

        CacheLocalUtil.put(key, result, remainTimeToLive);

        return result;

    }

    /**
     * 获取：一般类型的缓存从 map里
     */
    @SneakyThrows
    @NotNull
    public static <T> T getSecondMap(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @NotNull String secondKey, @Nullable T defaultResult, @Nullable Func0<T> func0) {

        if (StrUtil.isBlank(secondKey)) {
            throw new RuntimeException("操作失败：获取时，secondKey不能为空，请联系管理员");
        }

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        T result = CacheLocalUtil.getSecondMap(key, secondKey);

        if (result != null) {

            log.info("{}：返回 本地缓存", key);
            return result;

        }

        RMap<String, T> rMap = redissonClient.getMap(key);

        result = rMap.get(secondKey);

        if (result == null) {

            if (func0 != null) {

                log.info("{}：读取提供者的数据", key);
                result = func0.call();

            }

        } else {

            log.info("{}：加入 本地缓存，并返回 redis缓存", key);
            CacheLocalUtil.putSecondMap(key, secondKey, result);
            return result;

        }

        result = CacheHelper.checkAndReturnResult(result, defaultResult); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        rMap.put(secondKey, result); // 先加入到 redis里

        log.info("{}：加入 本地缓存", key);
        CacheLocalUtil.putSecondMap(key, secondKey, result);

        return result;

    }

    /**
     * 获取：map类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Map<?, ?>> T getMap(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable T defaultResult, @Nullable Func0<T> func0) {

        return getMap(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 获取：map类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Map<?, ?>> T getMap(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @Nullable T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        T result = CacheLocalUtil.get(key);

        if (CollUtil.isNotEmpty(result)) {

            log.info("{}：返回 本地缓存", key);
            return result;

        }

        result = (T)redissonClient.getMap(key).readAllMap();

        if (CollUtil.isNotEmpty(result)) {

            log.info("{}：加入 本地缓存，并返回 redis缓存", key);
            CacheLocalUtil.put(key, result, -1);
            return result;

        }

        if (func0 != null) {

            log.info("{}：读取提供者的数据", key);
            result = func0.call();

        }

        result = CacheHelper.checkAndReturnResult(result, defaultResult); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        T finalResult = result;
        RedissonUtil.batch((batch) -> {

            batch.getMap(key).deleteAsync();
            batch.getMap(key).putAllAsync(finalResult);

        });

        log.info("{}：加入 本地缓存", key);
        CacheLocalUtil.put(key, result, -1);

        return result;

    }

    /**
     * 获取：collection类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Collection<?>> T getCollection(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable T defaultResult, @Nullable Func0<T> func0) {

        return getCollection(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 获取：collection类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Collection<?>> T getCollection(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @Nullable T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        T result = CacheLocalUtil.get(key);

        if (CollUtil.isNotEmpty(result)) {

            log.info("{}：返回 本地缓存", key);
            return result;

        }

        boolean setFlag = defaultResult instanceof Set;

        if (setFlag) {

            result = (T)redissonClient.getSet(key).readAll();

        } else {

            result = (T)redissonClient.getList(key).readAll();

        }

        if (CollUtil.isNotEmpty(result)) {

            log.info("{}：加入 本地缓存，并返回 redis缓存", key);
            CacheLocalUtil.put(key, result, -1);
            return result;

        }

        if (func0 != null) {

            log.info("{}：读取提供者的数据", key);
            result = func0.call();

        }

        result = CacheHelper.checkAndReturnResult(result, defaultResult); // 检查并设置值

        log.info("{}：加入 redis缓存", key);

        T finalResult = result;
        RedissonUtil.batch((batch) -> {

            if (setFlag) {

                batch.getSet(key).deleteAsync();
                batch.getSet(key).addAllAsync(finalResult);

            } else {

                batch.getList(key).deleteAsync();
                batch.getList(key).addAllAsync(finalResult);

            }

        });

        log.info("{}：加入 本地缓存", key);
        CacheLocalUtil.put(key, result, -1);

        return result;

    }

}
