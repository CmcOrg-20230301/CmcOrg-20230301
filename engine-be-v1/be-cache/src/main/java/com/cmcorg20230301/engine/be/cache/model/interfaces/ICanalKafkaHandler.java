package com.cmcorg20230301.engine.be.cache.model.interfaces;

import com.cmcorg20230301.engine.be.cache.model.dto.CanalKafkaDTO;
import org.redisson.api.RBatch;

import java.util.Set;

/**
 * 处理：canal的 kafka消息
 */
public interface ICanalKafkaHandler {

    /**
     * 类似：cmcorg.sys_menu 格式
     */
    Set<ICanalKafkaHandlerKey> getKeySet();

    /**
     * 如果 keySet 包含，则进行处理
     */
    void handler(final CanalKafkaDTO dto, final RBatch batch);

}
