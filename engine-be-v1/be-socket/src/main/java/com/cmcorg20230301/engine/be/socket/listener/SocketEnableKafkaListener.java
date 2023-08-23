package com.cmcorg20230301.engine.be.socket.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.socket.model.configuration.ISocketEnable;
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
 * socket禁用的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}", batch = "true")
@Slf4j(topic = LogTopicConstant.SOCKET)
public class SocketEnableKafkaListener {

    public static final List<String> TOPIC_LIST = CollUtil.newArrayList(KafkaTopicEnum.SOCKET_ENABLE_TOPIC.name());

    private static List<ISocketEnable> iSocketEnableList;

    @Autowired(required = false)
    public void setISocketEnableList(List<ISocketEnable> iSocketEnableList) {
        SocketEnableKafkaListener.iSocketEnableList = iSocketEnableList;
    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        Set<Long> socketIdSet = recordList.stream() //
            .map(it -> JSONUtil.toList(it, Long.class)) //
            .flatMap(Collection::stream)  //
            .collect(Collectors.toSet());

        if (CollUtil.isNotEmpty(iSocketEnableList)) {

            for (ISocketEnable item : iSocketEnableList) {

                // 执行：处理
                item.handle(socketIdSet);

            }

        }

        acknowledgment.acknowledge(); // ack消息

    }

}