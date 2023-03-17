package com.cmcorg20230301.engine.be.cache.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.engine.be.cache.util.CanalKafkaListenerHelper;
import com.cmcorg20230301.engine.be.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.engine.be.kafka.util.KafkaUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.redisson.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * canal的 kafka监听器
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{__listener.GROUP_ID}", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE_CANAL)
public class CanalKafkaListener {

    public static final List<String> TOPIC_LIST = CollUtil.newArrayList(KafkaTopicEnum.CANAL_TOPIC_ENGINE_BE.name());

    public static final String GROUP_ID = KafkaTopicEnum.CANAL_TOPIC_ENGINE_BE.name();

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        CanalKafkaListenerHelper.CanalKafkaResult result = new CanalKafkaListenerHelper.CanalKafkaResult();

        RedissonUtil.batch((batch) -> {

            for (String item : recordList) {

                CanalKafkaDTO dto = JSONUtil.toBean(item, CanalKafkaDTO.class);

                // 处理：表名
                String tableName = CanalKafkaListenerHelper.handleTableName(dto.getTable());

                // 表的全路径名
                String fullTableName = dto.getDatabase() + "." + tableName;

                List<CanalKafkaListenerHelper.ICanalKafkaHandler> iCanalKafkaHandlerList =
                    CanalKafkaListenerHelper.get(fullTableName);

                if (CollUtil.isNotEmpty(iCanalKafkaHandlerList)) {

                    for (CanalKafkaListenerHelper.ICanalKafkaHandler subItem : iCanalKafkaHandlerList) {

                        subItem.handler(dto, batch, result); // 进行处理

                    }

                }

            }

        });

        if (CollUtil.isNotEmpty(result.getRemoveLocalCacheKeySet())) {

            log.info("canal：发送：本地缓存移除：removeLocalCacheKeySet：{}", result.getRemoveLocalCacheKeySet());

            // 发送：本地缓存移除的 topic
            KafkaUtil.sendLocalCacheRemoveTopic(result.getRemoveLocalCacheKeySet());

        }

        acknowledgment.acknowledge(); // ack消息

    }

}

