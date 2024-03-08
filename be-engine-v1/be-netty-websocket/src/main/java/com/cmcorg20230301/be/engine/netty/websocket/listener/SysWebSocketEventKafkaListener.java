package com.cmcorg20230301.be.engine.netty.websocket.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.netty.websocket.util.WebSocketUtil;
import com.cmcorg20230301.be.engine.security.model.bo.SysWebSocketEventBO;
import com.cmcorg20230301.be.engine.security.util.KafkaHelper;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.TryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.collection.CollUtil;

/**
 * webSocket事件的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}",
    batch = "true")
public class SysWebSocketEventKafkaListener {

    public static final List<String> TOPIC_LIST =
        CollUtil.newArrayList(KafkaTopicEnum.SYS_WEB_SOCKET_EVENT_TOPIC.name());

    // 目的：Long 转 String，Enum 转 code
    private static ObjectMapper objectMapper;

    public SysWebSocketEventKafkaListener(ObjectMapper objectMapper) {

        SysWebSocketEventKafkaListener.objectMapper = objectMapper;

    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        // ack消息
        TryUtil.tryCatchFinally(() -> {

            if (KafkaHelper.notHandleKafkaTopCheck(TOPIC_LIST)) {
                return;
            }

            if (CollUtil.isNotEmpty(recordList)) {

                MyThreadUtil.execute(() -> {

                    for (String item : recordList) {

                        TryUtil.tryCatch(() -> {

                            SysWebSocketEventBO<?> sysWebSocketEventBO =
                                objectMapper.readValue(item, SysWebSocketEventBO.class);

                            // 发送：webSocket消息
                            WebSocketUtil.send(sysWebSocketEventBO);

                        });

                    }

                });

            }

        }, acknowledgment::acknowledge);

    }

}
