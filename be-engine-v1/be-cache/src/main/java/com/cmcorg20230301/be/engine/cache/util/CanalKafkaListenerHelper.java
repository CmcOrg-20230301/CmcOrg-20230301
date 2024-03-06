package com.cmcorg20230301.be.engine.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * canal-kafka监听器的帮助类
 */
@Component
@Slf4j(topic = LogTopicConstant.CACHE)
public class CanalKafkaListenerHelper {

    // key：database.tableName，value：iCanalKafkaHandlerList
    public static final Map<String, List<ICanalKafkaHandler>> CANAL_KAFKA_HANDLER_MAP = new HashMap<>();

    public interface ICanalKafkaHandler {

        /**
         * 要处理的，表的全路径名，例如：database.tableName
         */
        Set<String> getFullTableNameSet();

        /**
         * 如果 fullTableNameSet 包含，则进行处理
         */
        void handler(final CanalKafkaDTO dto, final RBatch batch, final CanalKafkaResult result);

    }

    @Data
    public static class CanalKafkaResult {

        /**
         * 需要本地缓存移除的 keySet
         */
        private Set<String> removeLocalCacheKeySet = new HashSet<>();

    }

    public CanalKafkaListenerHelper(
        @Autowired(required = false) @Nullable List<ICanalKafkaHandler> iCanalKafkaHandlerList) {

        if (CollUtil.isNotEmpty(iCanalKafkaHandlerList)) {

            for (ICanalKafkaHandler item : iCanalKafkaHandlerList) {

                put(item); // 添加到：map里面

                log.info("CANAL_KAFKA_HANDLER_MAP，长度：{}，className：{}",
                    CANAL_KAFKA_HANDLER_MAP.size(),
                    item.getClass().getSimpleName());

            }

        }

    }

    /**
     * 添加
     */
    public static void put(ICanalKafkaHandler iCanalKafkaHandler) {

        if (CollUtil.isEmpty(iCanalKafkaHandler.getFullTableNameSet())) {
            return;
        }

        for (String item : iCanalKafkaHandler.getFullTableNameSet()) {

            List<ICanalKafkaHandler> iCanalKafkaHandlerList =
                CANAL_KAFKA_HANDLER_MAP.computeIfAbsent(item, k -> new ArrayList<>());

            iCanalKafkaHandlerList.add(iCanalKafkaHandler); // 添加到：集合里

        }

    }

    /**
     * 获取
     */
    public static List<ICanalKafkaHandler> get(String fullTableName) {

        return CANAL_KAFKA_HANDLER_MAP.get(fullTableName);

    }

    /**
     * 处理：表名
     */
    public static String handleTableName(String key) {

        String underlineStr = "_";

        List<String> splitTrimList = StrUtil.splitTrim(key, underlineStr);

        if (CollUtil.isNotEmpty(splitTrimList)) {

            String tableIndexStr = splitTrimList.get(splitTrimList.size() - 1);

            if (NumberUtil.isNumber(tableIndexStr)) {

                splitTrimList.remove(splitTrimList.size() - 1); // 移除：最后一个元素，即：分表的 index

                return CollUtil.join(splitTrimList, underlineStr); // 重新：组装 key

            }

        }

        return key;

    }

}
