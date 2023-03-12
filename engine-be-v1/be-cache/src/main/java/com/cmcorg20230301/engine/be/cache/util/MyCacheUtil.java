package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.MapUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.redisson.model.interfaces.IRedisKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 缓存工具类
 */
@Component
@Slf4j(topic = LogTopicConstant.CACHE)
public class MyCacheUtil {

    // 本地缓存
    private static final Cache<Enum<? extends IRedisKey>, Object> LOCAL_CACHE = CacheUtil.newLRUCache(2000000);

    private static RedissonClient redissonClient;

    public MyCacheUtil(RedissonClient redissonClient) {

        MyCacheUtil.redissonClient = redissonClient;

    }

    /**
     * 通过：key，移除：本地缓存
     */
    public static void removeLocalCacheByKey(@NotNull Enum<? extends IRedisKey> key) {

        LOCAL_CACHE.remove(key);

    }

    @NotNull
    public static <T> Map<Long, T> getDefaultLongTMap() {

        Map<Long, T> defaultResultMap = MapUtil.newHashMap();
        defaultResultMap.put(BaseConstant.SYS_ID, null);
        return defaultResultMap;

    }

    @NotNull
    public static <T> Map<Long, Set<T>> getDefaultLongSetMap() {

        Map<Long, Set<T>> defaultResultMap = MapUtil.newHashMap();
        defaultResultMap.put(BaseConstant.SYS_ID, new HashSet<>());
        return defaultResultMap;

    }

    @NotNull
    public static <T> Map<Long, List<T>> getDefaultLongListMap() {

        Map<Long, List<T>> defaultResultMap = MapUtil.newHashMap();
        defaultResultMap.put(BaseConstant.SYS_ID, new ArrayList<>());
        return defaultResultMap;

    }

    @NotNull
    public static <T> List<T> getDefaultResultList() {

        List<T> defaultResultList = new ArrayList<>();
        defaultResultList.add(null); // 注意：这里要小心使用
        return defaultResultList;

    }

    @NotNull
    public static <T> Set<T> getDefaultResultSet() {

        Set<T> defaultResultSet = new HashSet<>();
        defaultResultSet.add(null); // 注意：这里要小心使用
        return defaultResultSet;

    }

    /**
     * 是否是：默认的集合返回值
     */
    public static <T> boolean defaultCollectionResultFlag(Collection<T> collection) {

        if (collection == null) {
            return true;
        }

        if (collection.size() != 1) {
            return false;
        }

        if (collection instanceof List) {
            return ((List<T>)collection).get(0) == null;
        }

        return collection.contains(null);

    }

    /**
     * 检查：result，如果 result为空，则使用 defaultResult
     */
    @NotNull
    private static <T> T checkAndReturnResult(T result, T defaultResult) {

        if (defaultResult == null) {

            throw new RuntimeException("操作失败：defaultResult == null"); // 不能为 null，目的：防止缓存不写入数据

        } else if (defaultResult instanceof Map && CollUtil.isEmpty((Map<?, ?>)defaultResult)) {

            throw new RuntimeException("操作失败：defaultResult为 Map类型，但是长度为 0"); // 不能为 null，目的：防止缓存不写入数据

        } else if (defaultResult instanceof Iterator && CollUtil.isEmpty((Iterator<?>)defaultResult)) {

            throw new RuntimeException("操作失败：defaultResult为 Iterator类型，但是长度为 0"); // 不能为 null，目的：防止缓存不写入数据

        }

        if (result == null) {

            log.info("MyCacheUtil：设置默认值：{}", defaultResult);
            result = defaultResult;

        } else if (result instanceof Map && CollUtil.isEmpty((Map<?, ?>)result)) {

            log.info("MyCacheUtil：Map设置默认值：{}", defaultResult);
            result = defaultResult;

        } else if (result instanceof Iterator && CollUtil.isEmpty((Iterator<?>)result)) {

            log.info("MyCacheUtil：Iterator设置默认值：{}", defaultResult);
            result = defaultResult;

        }

        return result;

    }

    /**
     * 获取：一般类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T> T getCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @NotNull T defaultResult,
        @NotNull Func0<T> func0) {

        String key = redisKeyEnum.name();

        T result = (T)LOCAL_CACHE.get(redisKeyEnum);

        if (result != null) {
            log.info("{}：返回 本地缓存", key);
            return result;
        }

        result = (T)redissonClient.getBucket(key).get();

        if (result == null) {

            log.info("{}：读取数据库数据", key);
            result = func0.call();

        } else {

            log.info("{}：加入 本地缓存，并返回 redis缓存", key);
            LOCAL_CACHE.put(redisKeyEnum, result);
            return result;

        }

        result = checkAndReturnResult(result, defaultResult); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        redissonClient.getBucket(key).set(result); // 先加入到 redis里

        log.info("{}：加入 本地缓存", key);
        LOCAL_CACHE.put(redisKeyEnum, result);

        return result;

    }

    /**
     * 获取：map类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Map<?, ?>> T getMapCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @NotNull T defaultResultMap, @NotNull Func0<T> func0) {

        String key = redisKeyEnum.name();

        T resultMap = (T)LOCAL_CACHE.get(redisKeyEnum);

        if (CollUtil.isNotEmpty(resultMap)) {
            log.info("{}：返回 本地缓存", key);
            return resultMap;
        }

        if (redissonClient.getMap(key).isExists()) {

            resultMap = (T)redissonClient.getMap(key).readAllMap();

            if (CollUtil.isNotEmpty(resultMap)) {

                log.info("{}：加入 本地缓存，并返回 redis缓存", key);
                LOCAL_CACHE.put(redisKeyEnum, resultMap);
                return resultMap;

            }

        }

        log.info("{}：读取数据库数据", key);
        resultMap = func0.call();

        resultMap = checkAndReturnResult(resultMap, defaultResultMap); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        RBatch batch = redissonClient.createBatch(); // 先加入到 redis里
        batch.getMap(key).deleteAsync();
        batch.getMap(key).putAllAsync(resultMap);
        batch.execute();

        log.info("{}：加入 本地缓存", key);
        LOCAL_CACHE.put(redisKeyEnum, resultMap);

        return resultMap;

    }

    /**
     * 获取：Collection类型的缓存
     */
    @SneakyThrows
    @NotNull
    public static <T extends Collection<?>> T getCollectionCache(@NotNull Enum<? extends IRedisKey> redisKeyEnum,
        @NotNull T defaultResultCollection, @NotNull Func0<T> func0) {

        String key = redisKeyEnum.name();

        T resultList = (T)LOCAL_CACHE.get(redisKeyEnum);

        if (CollUtil.isNotEmpty(resultList)) {
            log.info("{}：返回 本地缓存", key);
            return resultList;
        }

        resultList = (T)redissonClient.getList(key).readAll();

        if (CollUtil.isNotEmpty(resultList)) {

            log.info("{}：加入 本地缓存，并返回 redis缓存", key);
            LOCAL_CACHE.put(redisKeyEnum, resultList);
            return resultList;

        }

        log.info("{}：读取数据库数据", key);
        resultList = func0.call();

        resultList = checkAndReturnResult(resultList, defaultResultCollection); // 检查并设置值

        log.info("{}：加入 redis缓存", key);
        RBatch batch = redissonClient.createBatch(); // 先加入到 redis里
        batch.getList(key).deleteAsync();
        batch.getList(key).addAllAsync(resultList);
        batch.execute();

        log.info("{}：加入 本地缓存", key);
        LOCAL_CACHE.put(redisKeyEnum, resultList);

        return resultList;

    }

}
