package com.cmcorg20230301.engine.be.cache.model.interfaces;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.cache.properties.CacheProperties;
import com.cmcorg20230301.engine.be.redisson.model.interfaces.IRedisKey;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 本地缓存和 canal对 kafka消息的帮助类
 */
public interface ILocalCacheAndCanalKafkaHelper extends ICanalKafkaHandlerKey {

    /**
     * 表名，备注：会截取后半段，判断是否是数字，例如：sys_user_0，则会变成：sys_user
     */
    String getName();

    /**
     * 要移除的 redisKeySet，还会移除本地缓存，备注：调用：CanalKafkaHandlerUtil#putCanalKafkaHandlerMap，这个方法会默认添加 ICanalKafkaHandler，进行删除
     */
    Set<Enum<? extends IRedisKey>> getDeleteRedisKeyEnumSet();

    @Override
    @Nullable
    default String getKey(CacheProperties cacheProperties) {

        if (StrUtil.isBlank(getName())) {
            return null;
        }

        return cacheProperties.getDatabaseName() + "." + getName();

    }

}
