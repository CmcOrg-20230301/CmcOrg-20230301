package com.cmcorg20230301.engine.be.redisson.model.interfaces;

/**
 * redis中 key的枚举类
 * 备注：如果是 redisson的锁 key，一定要备注：锁什么，例如：锁【用户主键 id】
 * 备注：【PRE_】开头，表示 key后面还要跟字符串
 * 备注：【_CACHE】结尾，表示 key后面不用跟字符串
 */
public interface IRedisKey {

    // 【PRE_】开头 ↓

    // 【_CACHE】结尾 ↓

    // 其他 ↓

}
