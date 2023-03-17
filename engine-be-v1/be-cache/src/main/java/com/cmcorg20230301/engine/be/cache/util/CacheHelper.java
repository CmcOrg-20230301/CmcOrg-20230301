package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Slf4j
public class CacheHelper {

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

        // 这里是检查：set等集合
        return collection.contains(null);

    }

    /**
     * 检查：result，如果 result为空，则使用 defaultResult
     */
    @NotNull
    public static <T> T checkAndReturnResult(T result, T defaultResult) {

        if (defaultResult == null) {

            throw new RuntimeException("操作失败：defaultResult == null"); // 不能为 null，目的：防止缓存不写入数据

        } else if (defaultResult instanceof Map && CollUtil.isEmpty((Map<?, ?>)defaultResult)) {

            throw new RuntimeException("操作失败：defaultResult为 Map类型，但是长度为 0"); // 不能为 null，目的：防止缓存不写入数据

        } else if (defaultResult instanceof Iterator && CollUtil.isEmpty((Iterator<?>)defaultResult)) {

            throw new RuntimeException("操作失败：defaultResult为 Iterator类型，但是长度为 0"); // 不能为 null，目的：防止缓存不写入数据

        }

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

        return result;

    }

}
