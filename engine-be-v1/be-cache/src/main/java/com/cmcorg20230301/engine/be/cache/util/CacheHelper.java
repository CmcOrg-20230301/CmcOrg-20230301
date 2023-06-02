package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 缓存帮助类
 */
@Slf4j
public class CacheHelper {

    /**
     * 获取：key
     */
    @NotNull
    public static String getKey(@NotNull Enum<? extends IRedisKey> redisKeyEnum, @Nullable String sufKey) {

        String key = redisKeyEnum.name();

        if (StrUtil.isNotBlank(sufKey)) {
            key = key + ":" + sufKey;
        }

        return key;

    }

    @NotNull
    public static <T> Map<Long, T> getDefaultLongMap() {

        Map<Long, T> result = MapUtil.newHashMap();
        result.put(BaseConstant.SYS_ID, null);

        return result;

    }

    @NotNull
    public static <T> Map<String, T> getDefaultStringMap() {

        Map<String, T> result = MapUtil.newHashMap();
        result.put("", null);

        return result;

    }

    @NotNull
    public static <T> Map<Long, Set<T>> getDefaultLongSetMap() {

        Map<Long, Set<T>> result = MapUtil.newHashMap();
        result.put(BaseConstant.SYS_ID, new HashSet<>());

        return result;

    }

    @NotNull
    public static <T> Map<String, Set<T>> getDefaultStringSetMap() {

        Map<String, Set<T>> result = MapUtil.newHashMap();
        result.put("", new HashSet<>());

        return result;

    }

    @NotNull
    public static <T> Map<String, List<T>> getDefaultStringListMap() {

        Map<String, List<T>> result = MapUtil.newHashMap();
        result.put("", new ArrayList<>());

        return result;

    }

    @NotNull
    public static <T> Map<Long, List<T>> getDefaultLongListMap() {

        Map<Long, List<T>> result = MapUtil.newHashMap();
        result.put(BaseConstant.SYS_ID, new ArrayList<>());

        return result;

    }

    @NotNull
    public static <T> List<T> getDefaultList() {

        List<T> result = new ArrayList<>();
        result.add(null); // 注意：这里要小心使用

        return result;

    }

    @NotNull
    public static <T> Set<T> getDefaultSet() {

        Set<T> result = new HashSet<>();
        result.add(null); // 注意：这里要小心使用

        return result;

    }

    /**
     * 是否是：默认的集合返回值
     */
    public static <T> boolean defaultCollectionFlag(Collection<T> collection) {

        if (collection == null) {
            return true;
        }

        if (collection.size() != 1) {
            return false;
        }

        if (collection instanceof List) {
            return ((List<T>)collection).get(0) == null;
        }

        // 这里是检查：set等集合
        return collection.contains(null);

    }

    /**
     * 检查：result，如果 result为空，则使用 defaultResult
     * 目的：防止设置不到值到缓存里面
     */
    @NotNull
    public static <T> T checkAndReturnResult(T result, T defaultResult) {

        if (result == null) {

            log.info("CacheHelper：设置默认值：{}", defaultResult);
            result = defaultResult;

        } else if (result instanceof Map && CollUtil.isEmpty((Map<?, ?>)result)) {

            log.info("CacheHelper：Map设置默认值：{}", defaultResult);
            result = defaultResult;

        } else if (result instanceof Iterator && CollUtil.isEmpty((Iterator<?>)result)) {

            log.info("CacheHelper：Iterator设置默认值：{}", defaultResult);
            result = defaultResult;

        }

        return checkResult(result);

    }

    /**
     * 检查：返回值
     */
    private static <T> T checkResult(T result) {

        if (result == null) {

            throw new RuntimeException("操作失败：result == null"); // 不能为 null，目的：防止缓存不写入数据

        } else if (result instanceof Map && CollUtil.isEmpty((Map<?, ?>)result)) {

            throw new RuntimeException("操作失败：defaultResult为 Map类型，但是长度为 0"); // 不能为 null，目的：防止缓存不写入数据

        } else if (result instanceof Iterator && CollUtil.isEmpty((Iterator<?>)result)) {

            throw new RuntimeException("操作失败：defaultResult为 Iterator类型，但是长度为 0"); // 不能为 null，目的：防止缓存不写入数据

        }

        return result;

    }

}
