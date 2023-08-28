package com.cmcorg20230301.be.engine.kafka.util;

import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyKeyValueSet;
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
    public static void send(KafkaTopicEnum kafkaTopicEnum, Object data) {

        kafkaTemplate.send(kafkaTopicEnum.name(), JSONUtil.toJsonStr(data));

    }

    /**
     * 发送消息：socket启用的 topic
     */
    public static void sendSocketEnableTopic(Set<Long> socketIdSet) {

        send(KafkaTopicEnum.SOCKET_ENABLE_TOPIC, socketIdSet);

    }

    /**
     * 发送消息：socket禁用的 topic
     */
    public static void sendSocketDisableTopic(Set<Long> socketIdSet) {

        send(KafkaTopicEnum.SOCKET_DISABLE_TOPIC, socketIdSet);

    }

    /**
     * 发送消息：支付状态发生改变时的 topic
     */
    public static void sendPayStatusChangeTopic(Object sysPayDO) {

        send(KafkaTopicEnum.SYS_PAY_TRADE_NOTIFY_TOPIC, sysPayDO);

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

    /**
     * 发送消息：本地缓存更新的 topic，针对往 map里面移除值
     */
    public static void sendLocalCacheRemoveMapTopic(NotEmptyKeyValueSet notEmptyKeyValueSet) {

        send(KafkaTopicEnum.LOCAL_CACHE_REMOVE_MAP_TOPIC, notEmptyKeyValueSet);

    }

}