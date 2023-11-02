package com.cmcorg20230301.be.engine.other.app.listener;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountWxReceiveMessageDTO;
import com.cmcorg20230301.be.engine.other.app.model.interfaces.ISysOtherAppOfficialAccountWxReceiveMessageHandle;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 处理微信公众号消息的 监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{__listener.GROUP_ID}", batch = "true")
@Slf4j(topic = LogTopicConstant.OTHER_APP_OFFICIAL_ACCOUNT_WX)
public class SysOtherAppOfficialAccountWxReceiveMessageListener {

    public static final List<String> TOPIC_LIST =
        CollUtil.newArrayList(KafkaTopicEnum.SYS_OTHER_APP_OFFICIAL_ACCOUNT_WX_RECEIVE_MESSAGE_TOPIC.name());

    public static final String GROUP_ID = KafkaTopicEnum.SYS_OTHER_APP_OFFICIAL_ACCOUNT_WX_RECEIVE_MESSAGE_TOPIC.name();

    // 目的：Long 转 String，Enum 转 code
    private static ObjectMapper objectMapper;

    @Nullable
    private static List<ISysOtherAppOfficialAccountWxReceiveMessageHandle>
        iSysOtherAppOfficialAccountWxReceiveMessageHandleList;

    public SysOtherAppOfficialAccountWxReceiveMessageListener(ObjectMapper objectMapper,
        @Nullable List<ISysOtherAppOfficialAccountWxReceiveMessageHandle> iSysOtherAppOfficialAccountWxReceiveMessageHandleList) {

        SysOtherAppOfficialAccountWxReceiveMessageListener.objectMapper = objectMapper;
        SysOtherAppOfficialAccountWxReceiveMessageListener.iSysOtherAppOfficialAccountWxReceiveMessageHandleList =
            iSysOtherAppOfficialAccountWxReceiveMessageHandleList;

    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        try {

            List<SysOtherAppOfficialAccountWxReceiveMessageDTO> sysOtherAppOfficialAccountWxReceiveMessageDTOList =

                recordList.stream().map(it -> {

                    try {

                        return objectMapper.readValue(it, SysOtherAppOfficialAccountWxReceiveMessageDTO.class);

                    } catch (Exception ignored) {

                    }

                    return null;

                }).filter(Objects::nonNull).collect(Collectors.toList());

            if (CollUtil.isNotEmpty(sysOtherAppOfficialAccountWxReceiveMessageDTOList) && CollUtil
                .isNotEmpty(iSysOtherAppOfficialAccountWxReceiveMessageHandleList)) {

                MyThreadUtil.execute(() -> {

                    for (SysOtherAppOfficialAccountWxReceiveMessageDTO item : sysOtherAppOfficialAccountWxReceiveMessageDTOList) {

                        try {

                            for (ISysOtherAppOfficialAccountWxReceiveMessageHandle subItem : iSysOtherAppOfficialAccountWxReceiveMessageHandleList) {

                                // 处理消息
                                subItem.handle(item);

                            }

                        } catch (Exception ignored) {

                        }

                    }

                });

            }

        } finally {

            acknowledgment.acknowledge(); // ack消息

        }

    }

}
