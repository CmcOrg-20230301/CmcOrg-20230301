package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 本地缓存工具类
 * 备注：不建议直接使用本类的方法，建议再封装一层
 */
@Slf4j(topic = LogTopicConstant.CACHE_LOCAL)
public class CacheLocalUtil {

    // 本地缓存：最近最久未使用缓存
    private static final Cache<String, Object> LOCAL_CACHE = CacheUtil.newLRUCache(2000000);

    /**
     * 添加：本地缓存
     */
    public static void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, Object value) {

        put(redisKeyEnum, null, value);

    }

    /**
     * 添加：本地缓存
     */
    public static void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey, Object value) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        put(key, value);

    }

    /**
     * 添加：本地缓存
     */
    public static void put(String key, Object value) {

        LOCAL_CACHE.put(key, value);

    }

    /**
     * 通过：redisKeyEnum，获取：本地缓存
     */
    @Nullable
    public static <T> T get(@NotNull Enum<? extends IRedisKey> redisKeyEnum) {

        return get(redisKeyEnum, null);

    }

    /**
     * 通过：redisKeyEnum，获取：本地缓存
     */
    @Nullable
    public static <T> T get(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        return get(key);

    }

    /**
     * 通过：key，获取：本地缓存
     */
    @Nullable
    public static <T> T get(String key) {

        return (T)LOCAL_CACHE.get(key);

    }

    /**
     * 通过：redisKeyEnum，移除：本地缓存
     */
    public static void remove(@NotNull Enum<? extends IRedisKey> redisKeyEnum) {

        remove(redisKeyEnum, null);

    }

    /**
     * 通过：redisKeyEnum，移除：本地缓存
     */
    public static void remove(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        remove(key); // 移除

    }

    /**
     * 通过：key，移除：本地缓存
     */
    public static void remove(String key) {

        LOCAL_CACHE.remove(key); // 移除

    }

    /**
     * 通过：keySet，移除：本地缓存
     */
    public static void removeAll(Set<String> keySet) {

        if (CollUtil.isEmpty(keySet)) {
            return;
        }

        for (String item : keySet) {

            remove(item); // 移除

        }

    }

}
