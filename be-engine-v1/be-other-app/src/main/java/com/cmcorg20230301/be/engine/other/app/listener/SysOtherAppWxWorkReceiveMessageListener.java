package com.cmcorg20230301.be.engine.other.app.listener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxWorkReceiveMessageDTO;
import com.cmcorg20230301.be.engine.other.app.model.interfaces.ISysOtherAppWxWorkReceiveMessageHandle;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.util.KafkaHelper;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.TryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理企业微信消息的 监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}",
    batch = "true")
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX_OFFICIAL_ACCOUNT)
public class SysOtherAppWxWorkReceiveMessageListener {

    public static final List<String> TOPIC_LIST =
        CollUtil.newArrayList(KafkaTopicEnum.SYS_OTHER_APP_WX_WORK_RECEIVE_MESSAGE_TOPIC.name());

    // 目的：Long 转 String，Enum 转 code
    private static ObjectMapper objectMapper;

    @Nullable
    public static List<ISysOtherAppWxWorkReceiveMessageHandle> iSysOtherAppWxWorkReceiveMessageHandleList;

    @Resource
    RedissonClient redissonClient;

    public SysOtherAppWxWorkReceiveMessageListener(ObjectMapper objectMapper,
        @Nullable List<ISysOtherAppWxWorkReceiveMessageHandle> iSysOtherAppWxWorkReceiveMessageHandleList) {

        SysOtherAppWxWorkReceiveMessageListener.objectMapper = objectMapper;
        SysOtherAppWxWorkReceiveMessageListener.iSysOtherAppWxWorkReceiveMessageHandleList =
            iSysOtherAppWxWorkReceiveMessageHandleList;

    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        TryUtil.tryCatchFinally(() -> {

            if (KafkaHelper.notHandleKafkaTopCheck(TOPIC_LIST)) {
                return;
            }

            List<SysOtherAppWxWorkReceiveMessageDTO> sysOtherAppWxWorkReceiveMessageDTOList =

                recordList.stream().map(it -> {

                    try {

                        return objectMapper.readValue(it, SysOtherAppWxWorkReceiveMessageDTO.class);

                    } catch (Exception ignored) {

                    }

                    return null;

                }).filter(Objects::nonNull).collect(Collectors.toList());

            if (CollUtil.isNotEmpty(sysOtherAppWxWorkReceiveMessageDTOList)
                && CollUtil.isNotEmpty(iSysOtherAppWxWorkReceiveMessageHandleList)) {

                MyThreadUtil.execute(() -> {

                    for (SysOtherAppWxWorkReceiveMessageDTO item : sysOtherAppWxWorkReceiveMessageDTOList) {

                        String msgIdStr = item.getMsgIdStr();

                        String redisKey =
                            BaseRedisKeyEnum.PRE_SYS_OTHER_APP_WX_WORK_RECEIVE_MESSAGE_ID.name() + msgIdStr;

                        RedissonUtil.doLock(redisKey, () -> {

                            boolean deleteFlag = redissonClient.<String>getBucket(redisKey).delete();

                            if (!deleteFlag) {
                                return;
                            }

                            TryUtil.tryCatch(() -> {

                                for (ISysOtherAppWxWorkReceiveMessageHandle subItem : iSysOtherAppWxWorkReceiveMessageHandleList) {

                                    // 处理消息
                                    subItem.handle(item);

                                }

                            });

                        });

                    }

                });

            }

        }, acknowledgment::acknowledge);

    }

}
