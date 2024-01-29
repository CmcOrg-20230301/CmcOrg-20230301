package com.cmcorg20230301.be.engine.model.model.constant;

/**
 * 日志主题的常量类
 */
public interface LogTopicConstant {

    String PRE_BE = PropertiesPrefixConstant.PRE_BE;

    String FILE_TYPE = PRE_BE + "file-type"; // 文件类型相关

    String NORMAL = PRE_BE + "normal"; // 没有指定 loggerName的日志，即：不以 be开头的 loggerName

    String CANAL = PRE_BE + "canal"; // canal相关

    String CACHE = PRE_BE + "cache"; // 缓存相关

    String CACHE_REDIS_KAFKA_LOCAL = PRE_BE + "cache-redis-kafka-local"; // redis-kafka-local缓存相关

    String CACHE_LOCAL = PRE_BE + "cache-local"; // 本地缓存相关

    String USER = PRE_BE + "user"; // 用户相关

    String USER_INFO = PRE_BE + "user-info"; // 用户信息相关

    String REQUEST = PRE_BE + "request"; // 请求相关

    String SOCKET = PRE_BE + "socket"; // socket相关

    String NETTY_WEB_SOCKET = PRE_BE + "netty-web-socket"; // netty-web-socket相关

    String NETTY_TCP_PROTOBUF = PRE_BE + "netty-tcp-protobuf"; // netty-tcp-protobuf相关

    String PAY = PRE_BE + "pay"; // 支付相关

    String PAY_APPLY = PAY + ".apply"; // 苹果支付相关

    String USER_WALLET = PRE_BE + "user-wallet"; // 用户钱包相关

    String OTHER_APP_WX = PRE_BE + "other-app-wx"; // 第三方应用-微信相关

    String OTHER_APP_OFFICIAL_ACCOUNT_MENU = PRE_BE + "other-app-official-account-menu"; // 第三方应用-公众号-菜单相关

    String OTHER_APP_WX_OFFICIAL_ACCOUNT = PRE_BE + "other-app-wx-official-account"; // 第三方应用-微信公众号相关

    String OTHER_APP_WX_WORK = PRE_BE + "other-app-wx-work"; // 第三方应用-企业微信相关

    String BAI_DU = PRE_BE + "baidu"; // 百度相关

}
