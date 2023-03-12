package com.cmcorg20230301.engine.be.cache.listener;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.cache.model.interfaces.ILocalCacheAndCanalKafkaHelper;
import com.cmcorg20230301.engine.be.cache.util.CanalKafkaHandlerUtil;
import com.cmcorg20230301.engine.be.cache.util.MyCacheUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.redisson.model.interfaces.IRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@KafkaListener(topics = {
    "#{T(com.cmcorg20230301.engine.be.kafka.enums.KafkaTopicEnum).LOCAL_CACHE_TOPIC.name()}"}, containerFactory = "dynamicGroupIdContainerFactory", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE)
public class LocalCacheKafkaListener {

    @Resource
    CanalKafkaHandlerUtil canalKafkaHandlerUtil;

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        Set<Enum<? extends IRedisKey>> redisKeyEnumSet =
            recordList.stream().map(it -> JSONUtil.toBean(it, new TypeReference<Set<String>>() {
            }, false)).flatMap(Collection::stream) // 去重
                .distinct().map(it -> canalKafkaHandlerUtil.getByKey(it))
                .filter(Objects::nonNull) // 找到：需要移除的 ILocalCacheAndCanalKafkaHelper
                .map(ILocalCacheAndCanalKafkaHelper::getDeleteRedisKeyEnumSet).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toSet());

        if (redisKeyEnumSet.size() != 0) {

            log.info("canal：清除 本地缓存：{}", redisKeyEnumSet);

            for (Enum<? extends IRedisKey> item : redisKeyEnumSet) {
                MyCacheUtil.removeLocalCacheByKey(item); // 清除本地缓存
            }

        }

        acknowledgment.acknowledge();

    }

}
