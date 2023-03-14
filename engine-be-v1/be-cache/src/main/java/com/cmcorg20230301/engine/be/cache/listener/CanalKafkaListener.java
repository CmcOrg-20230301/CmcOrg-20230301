package com.cmcorg20230301.engine.be.cache.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.engine.be.cache.model.interfaces.ICanalKafkaHandler;
import com.cmcorg20230301.engine.be.cache.model.interfaces.ICanalKafkaHandlerKey;
import com.cmcorg20230301.engine.be.cache.properties.CacheProperties;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.kafka.util.KafkaUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@KafkaListener(topics = {
    "#{T(com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum).CANAL_TOPIC_ENGINE_BE.name()}"}, groupId = "#{T(com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum).CANAL_TOPIC_ENGINE_BE.name()}", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE)
public class CanalKafkaListener {

    @Resource
    RedissonClient redissonClient;

    // key：database.table，value：ICanalKafkaHandler
    private static final Map<String, List<ICanalKafkaHandler>> CANAL_KAFKA_HANDLER_MAP = new HashMap<>();

    /**
     * 构造器：给 canalKafkaHandlerMap 添加元素
     */
    public CanalKafkaListener(List<ICanalKafkaHandler> iCanalKafkaHandlerList, CacheProperties cacheProperties) {

        if (CollUtil.isEmpty(iCanalKafkaHandlerList)) {
            return;
        }

        for (ICanalKafkaHandler item : iCanalKafkaHandlerList) {

            if (CollUtil.isEmpty(item.getKeySet())) {
                continue;
            }

            for (ICanalKafkaHandlerKey subItem : item.getKeySet()) {
                putCanalKafkaHandlerMap(subItem, cacheProperties, item);
            }

        }

    }

    /**
     * 给 canalKafkaHandlerMap 添加元素
     */
    public static void putCanalKafkaHandlerMap(ICanalKafkaHandlerKey iCanalKafkaHandlerKey,
        CacheProperties cacheProperties, ICanalKafkaHandler canalKafkaHandler) {

        List<ICanalKafkaHandler> handlerList = CANAL_KAFKA_HANDLER_MAP
            .computeIfAbsent(iCanalKafkaHandlerKey.getKey(cacheProperties), k -> new ArrayList<>());

        handlerList.add(canalKafkaHandler);

    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        if (CANAL_KAFKA_HANDLER_MAP.size() == 0) {
            acknowledgment.acknowledge();
            return;
        }

        RBatch batch = redissonClient.createBatch();

        Set<String> keySet = new HashSet<>();

        for (String item : recordList) {

            CanalKafkaDTO canalKafkaDTO = JSONUtil.toBean(item, CanalKafkaDTO.class);

            String key = canalKafkaDTO.getDatabase() + "." + canalKafkaDTO.getTable();

            // 处理 key
            key = handlerKey(key);

            List<ICanalKafkaHandler> handlerList = CANAL_KAFKA_HANDLER_MAP.get(key);

            if (CollUtil.isNotEmpty(handlerList)) {

                keySet.add(key);

                for (ICanalKafkaHandler subItem : handlerList) {
                    subItem.handler(canalKafkaDTO, batch); // 处理
                }

            }

        }

        batch.execute(); // 执行 batch

        if (keySet.size() != 0) {
            log.info("canal：清除 redis缓存：CanalKafkaHandlerKeyEnum中的，keySet：{}", keySet);
            KafkaUtil.send(KafkaTopicEnum.LOCAL_CACHE_TOPIC, keySet); // 发送：本地缓存处理的 topic
        }

        acknowledgment.acknowledge();

    }

    /**
     * 处理 key
     */
    private String handlerKey(String key) {

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

