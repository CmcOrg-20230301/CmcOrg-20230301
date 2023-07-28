package com.cmcorg20230301.engine.be.socket.listener;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * socket的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}", batch = "true")
@Slf4j(topic = LogTopicConstant.SOCKET)
public class SocketKafkaListener {

    public static final List<String> TOPIC_LIST = CollUtil.newArrayList(KafkaTopicEnum.SOCKET_DISABLE_TOPIC.name());

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        acknowledgment.acknowledge(); // ack消息

    }

}
