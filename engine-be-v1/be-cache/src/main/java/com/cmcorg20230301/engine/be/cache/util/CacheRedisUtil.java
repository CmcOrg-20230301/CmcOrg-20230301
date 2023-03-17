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
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * redis缓存工具类
 */
@Component
@Slf4j(topic = LogTopicConstant.CACHE_REDIS)
public class CacheRedisUtil {

    private static RedissonClient redissonClient;

    public CacheRedisUtil(RedissonClient redissonClient) {

        CacheRedisUtil.redissonClient = redissonClient;

    }

    /**
     * 获取：key
     */
    @NotNull
    public static String getKey(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey) {

        String key = redisKeyEnum.name();

        if (StrUtil.isNotBlank(sufKey)) {
            key = key + sufKey;
        }

        return key;

    }

    /**
     * 获取：一般类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T> T getCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @NotNull T defaultResult,
        @Nullable Func0<T> func0) {

        return getCache(redisKeyEnum, null, defaultResult, func0);

    }

    /**
     * 获取：一般类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T> T getCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @NotNull T defaultResult, @Nullable Func0<T> func0) {

        String key = CacheRedisUtil.getKey(redisKeyEnum, sufKey);

        T result = CacheLocalUtil.get(redisKeyEnum);

        if (result != null) {

            log.info("{}：返回 本地缓存", key);
            return result;

        }

        result = redissonClient.<T>getBucket(key).get();

        if (result == null) {

            if (func0 != null) {

                log.info("{}：读取提供者的数据", key);
                result = func0.call();

            }

        } else {

            log.info("{}：加入 本地缓存，并返回 redis缓存", key);

            CacheLocalUtil.put(redisKeyEnum, result);

            return result;

        }

        result = CacheHelper.checkAndReturnResult(result, defaultResult); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        redissonClient.getBucket(key).set(result); // 先加入到 redis里

        log.info("{}：加入 本地缓存", key);
        CacheLocalUtil.put(redisKeyEnum, result);

        return result;

    }

    /**
     * 获取：map类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Map<?, ?>> T getMapCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @NotNull T defaultResultMap, @Nullable Func0<T> func0) {

        return getMapCache(redisKeyEnum, null, defaultResultMap, func0);

    }

    /**
     * 获取：map类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Map<?, ?>> T getMapCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @NotNull T defaultResultMap, @Nullable Func0<T> func0) {

        String key = CacheRedisUtil.getKey(redisKeyEnum, sufKey);

        T result = CacheLocalUtil.get(redisKeyEnum);

        if (CollUtil.isNotEmpty(result)) {

            log.info("{}：返回 本地缓存", key);
            return result;

        }

        if (redissonClient.getMap(key).isExists()) {

            result = (T)redissonClient.getMap(key).readAllMap();

            if (CollUtil.isNotEmpty(result)) {

                log.info("{}：加入 本地缓存，并返回 redis缓存", key);
                CacheLocalUtil.put(redisKeyEnum, result);
                return result;

            }

        }

        if (func0 != null) {

            log.info("{}：读取提供者的数据", key);
            result = func0.call();

        }

        result = CacheHelper.checkAndReturnResult(result, defaultResultMap); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        T finalResult = result;
        RedissonUtil.batch((batch) -> {

            batch.getMap(key).deleteAsync();
            batch.getMap(key).putAllAsync(finalResult);

        });

        log.info("{}：加入 本地缓存", key);
        CacheLocalUtil.put(redisKeyEnum, result);

        return result;

    }

    /**
     * 获取：Collection类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Collection<?>> T getCollectionCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @NotNull T defaultResultCollection, @Nullable Func0<T> func0) {

        return getCollectionCache(redisKeyEnum, null, defaultResultCollection, func0);

    }

    /**
     * 获取：Collection类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Collection<?>> T getCollectionCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @Nullable String sufKey, @NotNull T defaultResultCollection, @Nullable Func0<T> func0) {

        String key = CacheRedisUtil.getKey(redisKeyEnum, sufKey);

        T result = CacheLocalUtil.get(redisKeyEnum);

        if (CollUtil.isNotEmpty(result)) {
            log.info("{}：返回 本地缓存", key);
            return result;
        }

        result = (T)redissonClient.getList(key).readAll();

        if (CollUtil.isNotEmpty(result)) {

            log.info("{}：加入 本地缓存，并返回 redis缓存", key);
            CacheLocalUtil.put(redisKeyEnum, result);
            return result;

        }

        if (func0 != null) {

            log.info("{}：读取提供者的数据", key);
            result = func0.call();

        }

        result = CacheHelper.checkAndReturnResult(result, defaultResultCollection); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        T finalResult = result;
        RedissonUtil.batch((batch) -> {

            batch.getList(key).deleteAsync();
            batch.getList(key).addAllAsync(finalResult);

        });

        log.info("{}：加入 本地缓存", key);
        CacheLocalUtil.put(redisKeyEnum, result);

        return result;

    }

}
