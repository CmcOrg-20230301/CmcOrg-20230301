package com.cmcorg20230301.be.engine.cache.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.cache.util.CacheLocalUtil;
import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
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

        try {

            Set<String> keySet = recordList.stream() //
                .map(it -> JSONUtil.toList(it, String.class)) //
                .flatMap(Collection::stream)  //
                .collect(Collectors.toSet());

            if (keySet.size() != 0) {

                log.info("kafka：清除 本地缓存：{}", keySet);
                CacheLocalUtil.removeAll(keySet); // 清除本地缓存

            }

        } finally {

            acknowledgment.acknowledge(); // ack消息

        }

    }

}
