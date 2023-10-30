package com.cmcorg20230301.be.engine.model.model.constant;

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

    String SOCKET = PRE_BE + "socket"; // socket相关

    String NETTY_WEB_SOCKET = PRE_BE + "netty-web-socket"; // netty-web-socket相关

    String NETTY_TCP_PROTOBUF = PRE_BE + "netty-tcp-protobuf"; // netty-tcp-protobuf相关

    String PAY = PRE_BE + "pay"; // 支付相关

    String USER_WALLET = PRE_BE + "user-wallet"; // 用户钱包相关

    String OTHER_APP_WX = PRE_BE + "other-app-wx"; // 第三方应用-微信相关

    String OTHER_APP_OFFICIAL_ACCOUNT_MENU = PRE_BE + "other-app-official-account-menu"; // 第三方应用-公众号-菜单相关

    String OTHER_APP_OFFICIAL_ACCOUNT_WX = PRE_BE + "other-app-official-account-wx"; // 第三方应用-公众号-微信相关

}
