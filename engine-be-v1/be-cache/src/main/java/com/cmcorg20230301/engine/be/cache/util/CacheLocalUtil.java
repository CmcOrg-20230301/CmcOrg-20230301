package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Slf4j(topic = LogTopicConstant.CACHE_LOCAL)
public class CacheLocalUtil {

    // 本地缓存：最近最久未使用缓存
    private static final Cache<String, Object> LOCAL_CACHE = CacheUtil.newLRUCache(2000000);

    /**
     * 添加：本地缓存
     */
    public static void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @NotNull Object value) {

        String key = CacheRedisUtil.getKey(redisKeyEnum, sufKey);

        LOCAL_CACHE.put(key, value);

    }

    /**
     * 通过：redisKeyEnum，获取：本地缓存
     */
    @Nullable
    public static <T> T get(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey) {

        String key = CacheRedisUtil.getKey(redisKeyEnum, sufKey);

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

        String key = CacheRedisUtil.getKey(redisKeyEnum, sufKey);

        remove(key); // 移除

    }

    /**
     * 通过：redisKeyEnumCollection，移除：本地缓存
     */
    public static void removeAll(@NotNull Set<String> keySet) {

        if (CollUtil.isEmpty(keySet)) {
            return;
        }

        for (String item : keySet) {

            remove(item); // 移除

        }

    }

    /**
     * 通过：redisKeyEnum，移除：本地缓存
     */
    public static void remove(String key) {

        if (StrUtil.isBlank(key)) {
            return;
        }

        LOCAL_CACHE.remove(key); // 移除

    }

}
