package com.cmcorg20230301.engine.be.cache.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.cache.util.CacheLocalUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyKeyValueSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 移除本地缓存的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE_LOCAL)
public class LocalCacheUpdateMapListener {

    public static final List<String> TOPIC_LIST =
        CollUtil.newArrayList(KafkaTopicEnum.LOCAL_CACHE_UPDATE_MAP_TOPIC.name());

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        for (String item : recordList) {

            NotEmptyKeyValueSet notEmptyKeyValueSet = JSONUtil.toBean(item, NotEmptyKeyValueSet.class);

            Set<NotEmptyKeyValueSet.KeyValue> keyValueSet = notEmptyKeyValueSet.getKeyValueSet();

            for (NotEmptyKeyValueSet.KeyValue subItem : keyValueSet) {

                CacheLocalUtil.put(item, subItem.getKey(), subItem.getValue()); // 更新：本地缓存

            }

        }

        acknowledgment.acknowledge(); // ack消息

    }

}
