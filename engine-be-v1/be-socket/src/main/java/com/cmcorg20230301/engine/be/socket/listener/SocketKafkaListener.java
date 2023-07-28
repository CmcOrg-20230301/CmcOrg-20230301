package com.cmcorg20230301.engine.be.socket.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.socket.model.configuration.ISocketDisable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * socket的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}", batch = "true")
@Slf4j(topic = LogTopicConstant.SOCKET)
public class SocketKafkaListener {

    public static final List<String> TOPIC_LIST = CollUtil.newArrayList(KafkaTopicEnum.SOCKET_DISABLE_TOPIC.name());

    private static List<ISocketDisable> iSocketDisableList;

    @Autowired(required = false)
    public void setISocketDisableList(List<ISocketDisable> iSocketDisableList) {
        SocketKafkaListener.iSocketDisableList = iSocketDisableList;
    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        Set<Long> socketIdSet = recordList.stream() //
            .map(it -> JSONUtil.toList(it, Long.class)) //
            .flatMap(Collection::stream)  //
            .collect(Collectors.toSet());

        for (ISocketDisable item : iSocketDisableList) {

            // 执行：处理
            item.handle(socketIdSet);

        }

        acknowledgment.acknowledge(); // ack消息

    }

}
