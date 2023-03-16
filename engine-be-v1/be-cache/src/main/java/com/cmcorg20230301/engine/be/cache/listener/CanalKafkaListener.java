package com.cmcorg20230301.engine.be.cache.listener;

import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@KafkaListener(topics = {
    "#{T(com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum).CANAL_TOPIC_ENGINE_BE.name()}"}, groupId = "#{T(com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum).CANAL_TOPIC_ENGINE_BE.name()}", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE)
public class CanalKafkaListener {

    @Resource
    RedissonClient redissonClient;

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        acknowledgment.acknowledge();

    }

}

