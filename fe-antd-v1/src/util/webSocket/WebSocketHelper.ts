import {IWebSocketMessage} from "@/util/webSocket/WebSocketUtil";
import {HeartBeatRequest, NETTY_WEB_SOCKET_HEART_BEAT} from "@/api/socket/WebSocket";
import {NettyWebSocketGetAllWebSocketUrl} from "@/api/http/NettyWebSocket";

/**
 * 处理：所有 webSocketUrl
 */
function handleAllWebSocketUrl(webSocketUrlArr: string[], resolve: (value: (PromiseLike<string> | string)) => void) {

    // webSocketId 和 延迟，单位：毫秒
    const webSocketIdAndTsObj: Record<string, number> = {}

    let minMs = -1; // 最低延迟，单位：毫秒

    let resWebSocketId = "" // 返回值：webSocketId

    // 获取：对应的延迟
    webSocketUrlArr.forEach(item => {

        const webSocket = new WebSocket(item);

        let beginTs;

        webSocket.onopen = (event) => {

            beginTs = new Date().getTime();

            HeartBeatRequest(webSocket); // 心跳检测，请求

        }

        webSocket.onmessage = (message: MessageEvent<string>) => {

            const webSocketMessage: IWebSocketMessage<any> = JSON.parse(message.data)

            if (webSocketMessage.uri === NETTY_WEB_SOCKET_HEART_BEAT) {

                const ms = new Date().getTime() - beginTs;

                webSocketIdAndTsObj[webSocketMessage.data] = ms

                // 获取：延迟最低的 webSocketId
                if (minMs === -1 || ms < minMs) {

                    minMs = ms

                    resWebSocketId = webSocketMessage.data

                }

            }

        }

    })

    setTimeout(() => {

        console.log('webSocketIdAndTsObj：', JSON.stringify(webSocketIdAndTsObj))

        resolve(resWebSocketId) // 返回值

    }, 3000)

}

/**
 * 获取：延迟最低的 webSocketId
 */
export function GetWebSocketId() {

    return new Promise<string>((resolve, reject) => {

        if (!window.WebSocket) {
            return reject(new Error('您的浏览器不支持 WebSocket协议，请更换浏览器再试'))
        }

        // 获取：所有的 webSocket连接
        NettyWebSocketGetAllWebSocketUrl().then(res => {

            // 处理：所有 webSocketUrl
            handleAllWebSocketUrl(res.data, resolve);

        })

    })

}

/**
 * 发送消息
 */
export function WebSocketSend<T>(webSocket: WebSocket | null, webSocketMessage: IWebSocketMessage<T>) {

    if (webSocket !== null && webSocket.readyState == webSocket.OPEN) {

        webSocket.send(JSON.stringify(webSocketMessage))

        return true;

    }

    return false;

}
