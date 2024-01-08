import {WebSocketSend} from "@/util/WebSocket/WebSocketHelper";
import {GetMyWebSocket} from "@/util/WebSocket/WebSocketUtil";

// 心跳检测
export const NETTY_WEB_SOCKET_HEART_BEAT = "/netty/webSocket/heartBeat"

// 关闭前端支付弹窗
export const SYS_PAY_CLOSE_MODAL = "/sys/pay/closeModal"

// 即时通讯收到消息
export const SYS_IM_SESSION_CONTENT_SEND = "/sys/im/session/content/send"

/**
 * 心跳检测，请求
 */
export function HeartBeatRequest(webSocket?: WebSocket | null) {

    if (webSocket == null) {
        webSocket = GetMyWebSocket()
    }

    return WebSocketSend(webSocket, {uri: NETTY_WEB_SOCKET_HEART_BEAT})

}
