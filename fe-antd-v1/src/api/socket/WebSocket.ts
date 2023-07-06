import {WebSocketSend} from "@/util/webSocket/WebSocketHelper";

// 心跳检测，响应
export const NETTY_WEB_SOCKET_HEART_BEAT_RESPONSE = "/netty/webSocket/heartBeat/response"

/**
 * 心跳检测，请求
 */
export function HeartBeatRequest(webSocket: WebSocket | null) {

    return WebSocketSend(webSocket, {uri: '/netty/webSocket/heartBeat/request'})

}
