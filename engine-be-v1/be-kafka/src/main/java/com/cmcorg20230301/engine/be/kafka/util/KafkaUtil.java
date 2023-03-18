package com.cmcorg20230301.engine.be.kafka.util;

import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
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
     * 发送消息：移除本地缓存
     */
    public static void sendLocalCacheRemoveTopic(Set<String> removeLocalCacheKeySet) {

        send(KafkaTopicEnum.LOCAL_CACHE_REMOVE_TOPIC, removeLocalCacheKeySet);

    }

}
