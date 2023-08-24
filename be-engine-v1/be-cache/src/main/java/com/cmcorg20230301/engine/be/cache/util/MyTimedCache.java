package com.cmcorg20230301.engine.be.cache.util;

import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.TimedCache;

/**
 * 定时过期缓存
 */
public class MyTimedCache<K, V> extends TimedCache<K, V> {

    public MyTimedCache(long timeout) {
        super(timeout);
    }

    /**
     * 获取：过期时间：0 表示已过期
     */
    public long getRemainTime(K key) {

        CacheObj<K, V> withoutLock = getWithoutLock(key);

        if (withoutLock == null) {
            return 0;
        }

        return (withoutLock.getLastAccess() + withoutLock.getTtl()) - System.currentTimeMillis();

    }

}
