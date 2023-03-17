package com.cmcorg20230301.engine.be.cache.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.cache.util.CacheLocalUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 移除本地缓存的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE_LOCAL)
public class LocalCacheRemoveListener {

    public static final List<String> TOPIC_LIST = CollUtil.newArrayList(KafkaTopicEnum.LOCAL_CACHE_REMOVE_TOPIC.name());

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        Set<Enum<? extends IRedisKey>> redisKeyEnumSet = recordList.stream() //
            .map(it -> JSONUtil.toBean(it, new TypeReference<Set<String>>() {
            }, false)) //
            .flatMap(Collection::stream) //
            .distinct()  // 去重
            .map(RedisKeyEnum::valueOf) //
            .collect(Collectors.toSet());

        if (redisKeyEnumSet.size() != 0) {

            log.info("canal：清除 本地缓存：{}", redisKeyEnumSet);
            CacheLocalUtil.removeAll(redisKeyEnumSet); // 清除本地缓存

        }

        acknowledgment.acknowledge(); // ack消息

    }

}
