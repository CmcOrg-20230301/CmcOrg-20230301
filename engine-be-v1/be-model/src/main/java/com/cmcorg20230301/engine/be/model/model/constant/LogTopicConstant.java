package com.cmcorg20230301.engine.be.model.model.constant;

/**
 * 日志主题的常量类
 */
public interface LogTopicConstant {

    String PRE_BE = PropertiesPrefixConstant.PRE_BE;

    String CACHE = PRE_BE + "cache"; // 缓存相关

    String USER = PRE_BE + "user"; // 用户相关

    String NORMAL = PRE_BE + "normal"; // 没有指定 loggerName 的日志

    String REQUEST = PRE_BE + "request"; // 请求相关

    String JAVA_TO_WEB = PRE_BE + "javaToWeb"; // 请求相关

}
