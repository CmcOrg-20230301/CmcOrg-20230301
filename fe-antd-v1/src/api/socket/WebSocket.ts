import {WebSocketSend} from "@/util/WebSocket/WebSocketHelper";
import {GetMyWebSocket} from "@/util/WebSocket/WebSocketUtil";

// 心跳检测
export const NETTY_WEB_SOCKET_HEART_BEAT = "/netty/webSocket/heartBeat"

/**
 * 心跳检测，请求
 */
export function HeartBeatRequest(webSocket?: WebSocket | null) {

    if (webSocket == null) {
        webSocket = GetMyWebSocket()
    }

    return WebSocketSend(webSocket, {uri: NETTY_WEB_SOCKET_HEART_BEAT})

}
