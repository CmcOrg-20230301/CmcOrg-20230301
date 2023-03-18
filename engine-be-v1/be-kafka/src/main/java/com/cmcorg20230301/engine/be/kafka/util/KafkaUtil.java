package com.cmcorg20230301.engine.be.kafka.util;

import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyKeyValueSet;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Kafka 工具类
 */
@Component
public class KafkaUtil {

    private static KafkaTemplate<String, String> kafkaTemplate;

    public KafkaUtil(KafkaTemplate<String, String> kafkaTemplate) {
        KafkaUtil.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送消息，备注：建议封装一层
     */
    public static <T> void send(KafkaTopicEnum kafkaTopicEnum, T data) {

        kafkaTemplate.send(kafkaTopicEnum.name(), JSONUtil.toJsonStr(data));

    }

    /**
     * 发送消息：本地缓存移除的 topic
     */
    public static void sendLocalCacheRemoveTopic(Set<String> removeLocalCacheKeySet) {

        send(KafkaTopicEnum.LOCAL_CACHE_REMOVE_TOPIC, removeLocalCacheKeySet);

    }

    /**
     * 发送消息：本地缓存更新的 topic，针对往 map里面设置值
     */
    public static void sendLocalCacheUpdateMapTopic(NotEmptyKeyValueSet notEmptyKeyValueSet) {

        send(KafkaTopicEnum.LOCAL_CACHE_UPDATE_MAP_TOPIC, notEmptyKeyValueSet);

    }

}
