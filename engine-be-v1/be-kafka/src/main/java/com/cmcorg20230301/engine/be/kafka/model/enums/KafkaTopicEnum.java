package com.cmcorg20230301.engine.be.kafka.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * kafka 主题枚举类
 */
@AllArgsConstructor
@Getter
public enum KafkaTopicEnum {

    CANAL_TOPIC_ENGINE_BE, // canal的 topic

    LOCAL_CACHE_REMOVE_TOPIC, // 本地缓存移除的 topic

    LOCAL_CACHE_UPDATE_MAP_TOPIC, // 本地缓存更新的 topic，针对往 map里面设置值

    LOCAL_CACHE_REMOVE_MAP_TOPIC, // 本地缓存更新的 topic，针对往 map里面移除值

    PAY_STATUS_CHANGE_TOPIC, // 支付状态发生改变时的 topic

    ;

}
