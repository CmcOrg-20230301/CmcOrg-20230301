import {WebSocketSend} from "@/util/WebSocket/WebSocketHelper";
import {GetMyWebSocket} from "@/util/WebSocket/WebSocketUtil";

// 心跳检测
export const NETTY_WEB_SOCKET_HEART_BEAT = "/netty/webSocket/heartBeat"

// 关闭前端支付弹窗
export const SYS_PAY_CLOSE_MODAL = "/sys/pay/closeModal"

// 即时通讯收到消息
export const SYS_IM_SESSION_CONTENT_SEND = "/sys/im/session/content/send"

// 即时通讯，加入新用户
export const SYS_IM_SESSION_REF_USER_JOIN_USER_ID_SET = "/sys/im/session/refUser/join/userIdSet"

// 开关控制台
export const SYS_SOCKET_REF_USER_CHANGE_CONSOLE_FLAG_BY_ID_SET = "/sys/socketRefUser/changeConsoleFlagByIdSet";

// 流程-流程实例全局参数发生改变时
export const SYS_ACTIVITI_PARAM_CHANGE = "/sys/im/activiti/param/change";

/**
 * 心跳检测，请求
 */
export function HeartBeatRequest(webSocket: WebSocket | null = GetMyWebSocket()) {

    return WebSocketSend(webSocket, {uri: NETTY_WEB_SOCKET_HEART_BEAT})

}
