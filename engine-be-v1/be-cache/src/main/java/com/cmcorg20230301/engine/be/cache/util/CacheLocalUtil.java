package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 本地缓存工具类
 * 备注：不建议直接使用本类的方法，建议再封装一层
 */
@Slf4j(topic = LogTopicConstant.CACHE_LOCAL)
@Component
public class CacheLocalUtil {

    private static final long TIMEOUT = BaseConstant.SECOND_20_EXPIRE_TIME;

    // 本地缓存：超时缓存，默认永不过期
    private static final MyTimedCache<String, Object> LOCAL_CACHE = new MyTimedCache<>(-1);

    static {

        // 定时清理 map，过期的条目
        LOCAL_CACHE.schedulePrune(TIMEOUT + BaseConstant.SECOND_3_EXPIRE_TIME);

    }

    @Resource
    RedissonClient redissonClient;

    /**
     * 定时任务，清除本地缓存不存在的 key
     */
    @Scheduled(fixedDelay = 10000)
    public void scheduledRemove() {

        // 需要移除的：本地缓存 keySet
        Set<String> removeKeySet = LOCAL_CACHE.keySet();

        RKeys rKeys = redissonClient.getKeys();

        for (String item : rKeys.getKeys()) {

            removeKeySet.remove(item); // 如果：redis还存在该值，则不需要本地缓存移除该 key

        }

        if (removeKeySet.size() != 0) {

            log.info("定时任务，移除本地缓存：{}", JSONUtil.toJsonStr(removeKeySet));

            for (String item : removeKeySet) {

                LOCAL_CACHE.remove(item);

            }

        }

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
    public static <T> void putSecondMap(@NotNull String key, @NotNull String secondKey, @NotNull T value) {

        Cache<String, T> secondMap = getSecondMap(key);

        secondMap.put(secondKey, value);

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
    public static <T> T getSecondMap(@NotNull String key, @NotNull String secondKey) {

        Cache<String, T> secondMap = getSecondMap(key);

        return secondMap.get(secondKey);

    }

    /**
     * 获取：二级map
     */
    @NotNull
    private static <T> Cache<String, T> getSecondMap(@NotNull String key) {

        return (Cache<String, T>)LOCAL_CACHE.get(key, false, () -> {
            return CacheUtil.newLFUCache(200 * 1000);
        });

    }

    /**
     * 通过：key，获取：过期时间
     */
    public static long getRemainTime(@NotNull String key) {

        return LOCAL_CACHE.getRemainTime(key);

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

    /**
     * 通过：key，移除：本地缓存从 map里
     */
    public static <T> void removeSecondMap(@NotNull String key, @NotNull String secondKey) {

        Cache<String, T> secondMap = getSecondMap(key);

        secondMap.remove(secondKey);

    }

}
