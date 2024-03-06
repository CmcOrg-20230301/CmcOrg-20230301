package com.cmcorg20230301.be.engine.other.app.listener;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxOfficialAccountReceiveMessageDTO;
import com.cmcorg20230301.be.engine.other.app.model.interfaces.ISysOtherAppWxOfficialAccountReceiveMessageHandle;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.util.KafkaHelper;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.TryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * 处理微信公众号消息的 监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}",
    batch = "true")
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX_OFFICIAL_ACCOUNT)
public class SysOtherAppWxOfficialAccountReceiveMessageListener {

    public static final List<String> TOPIC_LIST =
        CollUtil.newArrayList(
            KafkaTopicEnum.SYS_OTHER_APP_WX_OFFICIAL_ACCOUN_RECEIVE_MESSAGE_TOPIC.name());

    // 目的：Long 转 String，Enum 转 code
    private static ObjectMapper objectMapper;

    @Nullable
    private static List<ISysOtherAppWxOfficialAccountReceiveMessageHandle>
        iSysOtherAppWxOfficialAccountReceiveMessageHandleList;

    @Resource
    RedissonClient redissonClient;

    public SysOtherAppWxOfficialAccountReceiveMessageListener(ObjectMapper objectMapper,
        @Nullable List<ISysOtherAppWxOfficialAccountReceiveMessageHandle> iSysOtherAppWxOfficialAccountReceiveMessageHandleList) {

        SysOtherAppWxOfficialAccountReceiveMessageListener.objectMapper = objectMapper;
        SysOtherAppWxOfficialAccountReceiveMessageListener.iSysOtherAppWxOfficialAccountReceiveMessageHandleList =
            iSysOtherAppWxOfficialAccountReceiveMessageHandleList;

    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        TryUtil.tryCatchFinally(() -> {

            if (KafkaHelper.notHandleKafkaTopCheck(TOPIC_LIST)) {
                return;
            }

            List<SysOtherAppWxOfficialAccountReceiveMessageDTO> sysOtherAppWxOfficialAccountReceiveMessageDTOList =

                recordList.stream().map(it -> {

                    try {

                        return objectMapper.readValue(it,
                            SysOtherAppWxOfficialAccountReceiveMessageDTO.class);

                    } catch (Exception ignored) {

                    }

                    return null;

                }).filter(Objects::nonNull).collect(Collectors.toList());

            if (CollUtil.isNotEmpty(sysOtherAppWxOfficialAccountReceiveMessageDTOList)
                && CollUtil.isNotEmpty(
                iSysOtherAppWxOfficialAccountReceiveMessageHandleList)) {

                MyThreadUtil.execute(() -> {

                    for (SysOtherAppWxOfficialAccountReceiveMessageDTO item : sysOtherAppWxOfficialAccountReceiveMessageDTOList) {

                        String msgIdStr = item.getMsgIdStr();

                        String redisKey =
                            BaseRedisKeyEnum.PRE_SYS_OTHER_APP_WX_OFFICIAL_ACCOUNT_RECEIVE_MESSAGE_ID.name()
                                + msgIdStr;

                        RedissonUtil.doLock(redisKey, () -> {

                            boolean deleteFlag = redissonClient.<String>getBucket(redisKey)
                                .delete();

                            if (!deleteFlag) {
                                return;
                            }

                            TryUtil.tryCatch(() -> {

                                for (ISysOtherAppWxOfficialAccountReceiveMessageHandle subItem : iSysOtherAppWxOfficialAccountReceiveMessageHandleList) {

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
