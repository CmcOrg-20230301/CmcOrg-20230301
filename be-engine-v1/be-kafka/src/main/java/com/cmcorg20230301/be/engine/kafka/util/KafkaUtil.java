package com.cmcorg20230301.be.engine.kafka.util;

import java.util.Set;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.kafka.model.interfaces.IKafkaTopic;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyKeyValueSet;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

/**
 * Kafka 工具类
 */
@Component
public class KafkaUtil {

    private static KafkaTemplate<String, String> kafkaTemplate;

    // 目的：Long 转 String，Enum 转 code
    private static ObjectMapper objectMapper;

    public KafkaUtil(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {

        KafkaUtil.kafkaTemplate = kafkaTemplate;
        KafkaUtil.objectMapper = objectMapper;

    }

    /**
     * 发送消息，备注：建议封装一层
     */
    @SneakyThrows
    public static void send(IKafkaTopic iKafkaTopic, Object data) {

        if (data instanceof String) {

            kafkaTemplate.send(iKafkaTopic.name(), (String)data);

        } else {

            kafkaTemplate.send(iKafkaTopic.name(), objectMapper.writeValueAsString(data));

        }

    }

    /**
     * 发送消息：企业微信接收到消息之后，发送需要处理的对象 topic
     */
    public static void sendSysOtherAppWxWorkReceiveMessageDTO(Object sysOtherAppWxWorkReceiveMessageDTO) {

        send(KafkaTopicEnum.SYS_OTHER_APP_WX_WORK_RECEIVE_MESSAGE_TOPIC, sysOtherAppWxWorkReceiveMessageDTO);

    }

    /**
     * 发送消息：微信公众号接收到消息之后，发送需要处理的对象 topic
     */
    public static void
        sendSysOtherAppWxOfficialAccountReceiveMessageDTO(Object sysOtherAppWxOfficialAccountReceiveMessageDTO) {

        send(KafkaTopicEnum.SYS_OTHER_APP_WX_OFFICIAL_ACCOUN_RECEIVE_MESSAGE_TOPIC,
            sysOtherAppWxOfficialAccountReceiveMessageDTO);

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
     * 发送消息：webSocket事件的 topic
     */
    public static void sendSysWebSocketEventTopic(Object sysWebSocketEventBO) {

        send(KafkaTopicEnum.SYS_WEB_SOCKET_EVENT_TOPIC, sysWebSocketEventBO);

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
