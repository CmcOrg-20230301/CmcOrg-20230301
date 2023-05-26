package com.cmcorg20230301.engine.be.model.model.constant;

/**
 * 日志主题的常量类
 */
public interface LogTopicConstant {

    String PRE_BE = PropertiesPrefixConstant.PRE_BE;

    String NORMAL = PRE_BE + "normal"; // 没有指定 loggerName的日志

    String CANAL = PRE_BE + "canal"; // canal相关

    String CACHE = PRE_BE + "cache"; // 缓存相关

    String CACHE_REDIS_KAFKA_LOCAL = PRE_BE + "cache-redis-kafka-local"; // redis-kafka-local缓存相关

    String CACHE_LOCAL = PRE_BE + "cache-local"; // 本地缓存相关

    String USER = PRE_BE + "user"; // 用户相关

    String REQUEST = PRE_BE + "request"; // 请求相关

    String NETTY_WEB_SOCKET = PRE_BE + "netty-web-socket"; // netty-web-socket相关

}
