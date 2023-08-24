package com.cmcorg20230301.engine.be.pay.base.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.pay.base.model.configuration.ISysPayRefHandler;
import com.cmcorg20230301.engine.be.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayRefTypeEnum;
import lombok.extern.slf4j.Slf4j;
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

    private static final Map<SysPayRefTypeEnum, ISysPayRefHandler> SYS_PAY_REF_HANDLER_MAP = MapUtil.newHashMap();

    public SysPayTradeNotifyKafkaListener(@Autowired(required = false) List<ISysPayRefHandler> iSysPayRefHandlerList) {

        if (CollUtil.isNotEmpty(iSysPayRefHandlerList)) {

            for (ISysPayRefHandler item : iSysPayRefHandlerList) {

                SYS_PAY_REF_HANDLER_MAP.put(item.getSysPayRefType(), item);

            }

        }

    }

    @KafkaHandler
    public void receive(String recordStr, Acknowledgment acknowledgment) {

        SysPayDO sysPayDO = JSONUtil.toBean(recordStr, SysPayDO.class);

        ISysPayRefHandler iSysPayRefHandler = SYS_PAY_REF_HANDLER_MAP.get(sysPayDO.getRefType());

        if (iSysPayRefHandler != null) {

            // 处理：具体的业务
            iSysPayRefHandler.handle(sysPayDO);

        }

        acknowledgment.acknowledge(); // ack消息

    }

}
