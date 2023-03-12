package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.cache.listener.CanalKafkaListener;
import com.cmcorg20230301.engine.be.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.engine.be.cache.model.interfaces.ICanalKafkaHandler;
import com.cmcorg20230301.engine.be.cache.model.interfaces.ICanalKafkaHandlerKey;
import com.cmcorg20230301.engine.be.cache.model.interfaces.ILocalCacheAndCanalKafkaHelper;
import com.cmcorg20230301.engine.be.cache.properties.CacheProperties;
import com.cmcorg20230301.engine.be.redisson.model.interfaces.IRedisKey;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CanalKafkaHandlerUtil {

    // key：database.table，value：ILocalCacheAndCanalKafkaHelper
    private final static Map<String, ILocalCacheAndCanalKafkaHelper> I_LOCAL_CACHE_AND_CANAL_KAFKA_HANDLER_HASH_MAP =
        new HashMap<>();

    @Resource
    CacheProperties cacheProperties;

    public CanalKafkaHandlerUtil(
        @Autowired(required = false) List<ILocalCacheAndCanalKafkaHelper> iLocalCacheAndCanalKafkaHelperList) {

        if (CollUtil.isNotEmpty(iLocalCacheAndCanalKafkaHelperList)) {
            for (ILocalCacheAndCanalKafkaHelper item : iLocalCacheAndCanalKafkaHelperList) {
                if (CollUtil.isNotEmpty(item.getDeleteRedisKeyEnumSet())) {
                    putICanalKafkaHandlerKeyMap(item); // 添加到 map里面
                }
            }
        }

    }

    /**
     * 添加到 map里面
     */
    private void putICanalKafkaHandlerKeyMap(ILocalCacheAndCanalKafkaHelper item) {

        I_LOCAL_CACHE_AND_CANAL_KAFKA_HANDLER_HASH_MAP.put(item.getKey(cacheProperties), item);

    }

    @Nullable
    public ILocalCacheAndCanalKafkaHelper getByKey(String key) {

        if (StrUtil.isBlank(key)) {
            return null;
        }

        return I_LOCAL_CACHE_AND_CANAL_KAFKA_HANDLER_HASH_MAP.get(key);

    }

    /**
     * 添加元素到：canalKafkaHandlerMap里面
     */
    public void putCanalKafkaHandlerMap(ILocalCacheAndCanalKafkaHelper[] iLocalCacheAndCanalKafkaHelperArr) {

        for (ILocalCacheAndCanalKafkaHelper item : iLocalCacheAndCanalKafkaHelperArr) {

            // 添加一个 ICanalKafkaHandler，进行删除操作
            for (Enum<? extends IRedisKey> subItem : item.getDeleteRedisKeyEnumSet()) {

                // 给 canalKafkaHandlerMap 添加元素
                CanalKafkaListener.putCanalKafkaHandlerMap(item, cacheProperties, new ICanalKafkaHandler() {

                    @Override
                    public Set<ICanalKafkaHandlerKey> getKeySet() {
                        return null; // 备注：这里可以为 null，因为这里就是执行的 put操作，所以已经不需要这个返回值了
                    }

                    @Override
                    public void handler(CanalKafkaDTO dto, RBatch batch) {

                        if (dto.getType().dateUpdateFlag()) {
                            batch.getBucket(subItem.name()).deleteAsync();
                        }

                    }

                });

            }

            putICanalKafkaHandlerKeyMap(item); // 添加到 map里面

        }

    }

}
