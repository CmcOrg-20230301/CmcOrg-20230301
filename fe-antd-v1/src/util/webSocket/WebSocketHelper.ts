import {HeartBeatRequest, NETTY_WEB_SOCKET_HEART_BEAT} from "@/api/socket/WebSocket";
import {NettyWebSocketGetAllWebSocketUrl} from "@/api/http/NettyWebSocket";

export interface IWebSocketMessage<T> {
    uri: string // 路径
    data?: T // 数据
    code?: string // 响应代码，成功返回：200
    msg?: string // 响应描述
}

/**
 * 处理：所有 webSocketUrl
 */
function handleAllWebSocketUrl(webSocketUrlArr: string[], resolve: (value: (PromiseLike<string> | string)) => void) {

    // webSocketId 和 延迟，单位：毫秒
    const webSocketIdAndTsObj: Record<string, number> = {}

    let minMs = -1; // 最低延迟，单位：毫秒

    let resWebSocketId = "" // 返回值：webSocketId

    let checkMaxMs = 3000; // 检测时的最大延迟，单位：毫秒

    // 获取：对应的延迟
    webSocketUrlArr.forEach(item => {

        const webSocket = new WebSocket(item);

        let beginTs;

        webSocket.onopen = (event) => {

            beginTs = new Date().getTime();

            HeartBeatRequest(webSocket); // 心跳检测，请求

            setTimeout(() => {

                webSocket.close()

            }, checkMaxMs)

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

                webSocket.close()

            }

        }

    })

    setTimeout(() => {

        console.log('webSocketIdAndTsObj：', JSON.stringify(webSocketIdAndTsObj))

        resolve(resWebSocketId) // 返回值

    }, checkMaxMs)

}

/**
 * 获取：延迟最低的 webSocketId
 */
export function GetWebSocketId() {

    return new Promise<string | void>(resolve => {

        if (!window.WebSocket) {
            console.log('您的浏览器不支持 WebSocket协议，请更换浏览器再试')
            return;
        }

        // 获取：所有的 webSocket连接
        NettyWebSocketGetAllWebSocketUrl({

            headers: {

                hiddenErrorMsg: true

            } as any

        }).then(res => {

            // 处理：所有 webSocketUrl
            handleAllWebSocketUrl(res.data, resolve);

        }).catch(() => {

            resolve() // 备注：void就是 undefined

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
