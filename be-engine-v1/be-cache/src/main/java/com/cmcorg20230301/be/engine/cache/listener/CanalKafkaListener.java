package com.cmcorg20230301.be.engine.cache.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.be.engine.cache.util.CacheLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.CanalKafkaListenerHelper;
import com.cmcorg20230301.be.engine.kafka.model.enums.KafkaTopicEnum;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * canal的 kafka监听器
 * 备注：如果收不到消息，可以把 canal的 logs文件夹删除了，然后重启 canal即可
 */
@Component
@KafkaListener(topics = "#{__listener.TOPIC_LIST}", groupId = "#{__listener.GROUP_ID}", batch = "true")
@Slf4j(topic = LogTopicConstant.CANAL)
public class CanalKafkaListener {

    public static final List<String> TOPIC_LIST = CollUtil.newArrayList(KafkaTopicEnum.CANAL_TOPIC_ENGINE_BE.name());

    public static final String GROUP_ID = KafkaTopicEnum.CANAL_TOPIC_ENGINE_BE.name();

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        try {

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

            Set<String> removeLocalCacheKeySet = result.getRemoveLocalCacheKeySet();

            if (CollUtil.isNotEmpty(removeLocalCacheKeySet)) {

                log.info("canal：发送：本地缓存移除消息：removeLocalCacheKeySet：{}", removeLocalCacheKeySet);

                // 发送：本地缓存移除的 topic
                KafkaUtil.sendLocalCacheRemoveTopic(removeLocalCacheKeySet);

                CacheLocalUtil.removeAll(removeLocalCacheKeySet); // 清除本地缓存

            }

        } finally {

            acknowledgment.acknowledge(); // ack消息

        }

    }

}

