package com.cmcorg20230301.be.engine.pay.base.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPayRefHandler;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 支付订单回调通知的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{kafkaDynamicGroupIdConfiguration.getGroupId()}")
@Slf4j(topic = LogTopicConstant.PAY)
public class SysPayTradeNotifyKafkaListener {

    public static final List<String> TOPIC_LIST =
        CollUtil.newArrayList(KafkaTopicEnum.SYS_PAY_TRADE_NOTIFY_TOPIC.name());

    private static final Map<Integer, ISysPayRefHandler> SYS_PAY_REF_HANDLER_MAP = MapUtil.newHashMap();

    // 目的：Long 转 String，Enum 转 code
    private static ObjectMapper objectMapper;

    public SysPayTradeNotifyKafkaListener(
        @Autowired(required = false) @Nullable List<ISysPayRefHandler> iSysPayRefHandlerList,
        ObjectMapper objectMapper) {

        if (CollUtil.isNotEmpty(iSysPayRefHandlerList)) {

            for (ISysPayRefHandler item : iSysPayRefHandlerList) {

                SYS_PAY_REF_HANDLER_MAP.put(item.getSysPayRefType().getCode(), item);

            }

        }

        SysPayTradeNotifyKafkaListener.objectMapper = objectMapper;

    }

    @SneakyThrows
    @KafkaHandler
    public void receive(String recordStr, Acknowledgment acknowledgment) {

        try {

            SysPayDO sysPayDO = objectMapper.readValue(recordStr, SysPayDO.class);

            ISysPayRefHandler iSysPayRefHandler = SYS_PAY_REF_HANDLER_MAP.get(sysPayDO.getRefType());

            if (iSysPayRefHandler != null) {

                // 处理：具体的业务
                iSysPayRefHandler.handle(sysPayDO);

            }

        } finally {

            acknowledgment.acknowledge(); // ack消息

        }

    }

}
