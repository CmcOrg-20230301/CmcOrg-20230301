package com.cmcorg20230301.engine.be.cache.model.interfaces;

import com.cmcorg20230301.engine.be.cache.properties.CacheProperties;

/**
 * 获取：处理：canal的 kafka消息的 key
 */
public interface ICanalKafkaHandlerKey {

    String getKey(CacheProperties cacheProperties);

}
