package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存工具类
 * 备注：不建议直接使用本类的方法，建议再封装一层
 */
@Slf4j(topic = LogTopicConstant.CACHE_LOCAL)
public class CacheLocalUtil {

    private static final long TIMEOUT = BaseConstant.SECOND_20_EXPIRE_TIME;

    // 本地缓存：超时缓存，默认永不过期
    private static final TimedCache<String, Object> LOCAL_CACHE = CacheUtil.newTimedCache(BaseConstant.ZERO);

    static {

        // 定时清理 map，过期的条目
        LOCAL_CACHE.schedulePrune(TIMEOUT + BaseConstant.SECOND_3_EXPIRE_TIME);

    }

    /**
     * 添加：本地缓存
     */
    public static void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @NotNull Object value) {

        put(redisKeyEnum, null, value);

    }

    /**
     * 添加：本地缓存
     */
    public static void put(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey,
        @NotNull Object value) {

        String key = CacheHelper.getKey(redisKeyEnum, sufKey);

        put(key, value, -1);

    }

    /**
     * 添加：本地缓存
     */
    public static void put(@NotNull String key, @NotNull Object value, long timeToLive) {

        // 备注：这里是 < 0，都表示是永久
        LOCAL_CACHE.put(key, value, timeToLive);

    }

    /**
     * 添加：本地缓存到 map里
     */
    public static <T> void put(@NotNull String key, @NotNull String secondKey, @NotNull T value) {

        Map<String, T> map = getSecondMap(key);

        map.put(secondKey, value);

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
    public static <T> T get(@NotNull String key) {

        return (T)LOCAL_CACHE.get(key, false);

    }

    /**
     * 通过：key，获取：本地缓存从 map里
     */
    @Nullable
    public static <T> T get(@NotNull String key, @NotNull String secondKey) {

        Map<String, T> map = getSecondMap(key);

        return map.get(secondKey);

    }

    /**
     * 获取：map
     */
    private static <T> Map<String, T> getSecondMap(@NotNull String key) {

        return (Map<String, T>)LOCAL_CACHE.get(key, false, ConcurrentHashMap::new);

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
    public static void remove(@NotNull String key) {

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
