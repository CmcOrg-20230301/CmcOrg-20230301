package com.cmcorg20230301.be.engine.model.model.enums;

import com.cmcorg20230301.be.engine.model.model.vo.IWebSocketUri;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseWebSocketUriEnum implements IWebSocketUri {

    SYS_PAY_CLOSE_MODAL("/sys/pay/closeModal"), // 关闭支付弹窗

    SYS_IM_SESSION_CONTENT_SEND("/sys/im/session/content/send"), // im-发送消息

    SYS_IM_SESSION_REF_USER_JOIN_USER_ID_SET("/sys/im/session/refUser/join/userIdSet"), // im-聊天加入新的用户

    SYS_SOCKET_REF_USER_CHANGE_CONSOLE_FLAG_BY_ID_SET("/sys/socketRefUser/changeConsoleFlagByIdSet"), // 打开/关闭 控制台

    SYS_ACTIVITI_PARAM_CHANGE("/sys/im/activiti/param/change"), // 流程-流程实例全局参数发生改变时

    ;

    private final String uri;

}
