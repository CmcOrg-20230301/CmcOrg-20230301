package com.cmcorg20230301.be.engine.kafka.model.enums;

import com.cmcorg20230301.be.engine.kafka.model.interfaces.IKafkaTopic;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * kafka 主题枚举类
 */
@AllArgsConstructor
@Getter
public enum KafkaTopicEnum implements IKafkaTopic {

    // 第三方应用相关 ↓

    SYS_OTHER_APP_WX_OFFICIAL_ACCOUN_RECEIVE_MESSAGE_TOPIC, // 微信公众号接收到消息之后，发送需要处理的对象 topic

    // 第三方应用相关 ↑

    // 缓存相关 ↓

    CANAL_TOPIC_ENGINE_BE, // canal的 topic

    LOCAL_CACHE_REMOVE_TOPIC, // 本地缓存移除的 topic

    LOCAL_CACHE_UPDATE_MAP_TOPIC, // 本地缓存更新的 topic，针对往 map里面设置值

    LOCAL_CACHE_REMOVE_MAP_TOPIC, // 本地缓存更新的 topic，针对往 map里面移除值

    // 缓存相关 ↑

    // 支付相关 ↓

    SYS_PAY_TRADE_NOTIFY_TOPIC, // 支付订单回调通知时的 topic

    SYS_PAY_CLOSE_MODAL_TOPIC, // 关闭前端支付弹窗的 topic

    // 支付相关 ↑

    // socket相关 ↓

    SOCKET_DISABLE_TOPIC, // socket禁用的 topic，即：会断开所有的 socket连接

    SOCKET_ENABLE_TOPIC, // socket启用的 topic

    SYS_WEB_SOCKET_EVENT_TOPIC, // webSocket事件的 topic

    // socket相关 ↑

    ;

}
