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
    LOCAL_CACHE_TOPIC, // 本地缓存的 topic

    ;

}
