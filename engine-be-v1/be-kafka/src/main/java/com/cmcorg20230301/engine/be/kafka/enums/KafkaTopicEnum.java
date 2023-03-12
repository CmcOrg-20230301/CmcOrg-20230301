package com.cmcorg20230301.engine.be.kafka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * kafka 主题枚举类
 */
@AllArgsConstructor
@Getter
public enum KafkaTopicEnum {

    CANAL_TOPIC, // canal的 topic
    LOCAL_CACHE_TOPIC, // 本地缓存的 topic

    ;

}
