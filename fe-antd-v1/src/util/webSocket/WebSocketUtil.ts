import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {getAppDispatch} from "@/MyApp";
import {setWebSocketMessage, setWebSocketStatus} from "@/store/commonSlice";
import {GetWebSocketId, IWebSocketMessage, WebSocketSend} from "@/util/webSocket/WebSocketHelper";
import {NettyWebSocketGetWebSocketUrlById} from "@/api/http/NettyWebSocket";
import {HeartBeatRequest} from "@/api/socket/WebSocket";

let myWebSocket: WebSocket | null = null
let heartBeatInterval: any = null // 心跳检测，定时器

export function GetMyWebSocket() {
    return myWebSocket
}

// 备注：开发环境的超时时间设置长一点
const retryTime = import.meta.env.DEV ? 5000 : 2000

// 获取：webSocket的连接地址
async function GetWebSocketUrl(): Promise<string | null> {

    let webSocketUrl: string | null = null;

    await GetWebSocketId().then(async res => {

        if (!res) {
            return
        }

        // console.log('webSocketId：', res)

        await NettyWebSocketGetWebSocketUrlById({id: res, value: 101}).then(res => {

            webSocketUrl = res.data

        }).catch(() => {

            // 备注：如果不写这个 catch，并且请求出错时，那么就会报错，然后导致 webSocket重连失败

        })

    })

    return webSocketUrl;

}

// 关闭 webSocket
export function CloseWebSocket() {

    if (myWebSocket) {

        myWebSocket.close()
        myWebSocket = null

    }

}

// 连接 webSocket
export function ConnectWebSocket() {

    if (myWebSocket) {
        return
    }

    if (!localStorage.getItem(LocalStorageKey.JWT)) {
        return // 如果没有 jwt，则不重连了，目的：防止一直连
    }


    if (!window.WebSocket) {
        console.log('您的浏览器不支持 WebSocket协议，请更换浏览器再试')
        return;
    }

    GetWebSocketUrl().then(webSocketUrl => {

        if (!webSocketUrl) {

            setTimeout(() => {

                myWebSocket = null // 重置 webSocket对象，为了可以重新获取 webSocket连接地址
                ConnectWebSocket();

            }, retryTime)

            console.log('连接 WebSocket失败：暂无可用的服务器')
            return;

        }

        if (myWebSocket) {
            return
        }

        myWebSocket = new WebSocket(webSocketUrl)

        myWebSocket.onopen = (event) => {

            console.log(`WebSocket 连接 >> ${webSocketUrl?.substring(0, webSocketUrl!.indexOf("?"))}`)

            getAppDispatch()(setWebSocketStatus(true))

            if (heartBeatInterval) {
                clearInterval(heartBeatInterval)
            }

            heartBeatInterval = setInterval(() => {

                HeartBeatRequest(); // 心跳检测，请求

            }, 25 * 1000);

        }

        myWebSocket.onmessage = (message: MessageEvent<string>) => {

            const webSocketMessage: IWebSocketMessage<any> = JSON.parse(message.data)

            // 更新 redux里面 webSocket的值
            getAppDispatch()(setWebSocketMessage({} as IWebSocketMessage<any>)) // 先重置，再设置值

            setTimeout(() => {
                getAppDispatch()(setWebSocketMessage(webSocketMessage))
            }, 200)

        }

        myWebSocket.onclose = (event) => {

            console.log('WebSocket 关闭')

            getAppDispatch()(setWebSocketStatus(false))

            if (heartBeatInterval) {
                clearInterval(heartBeatInterval)
            }

            setTimeout(() => {

                myWebSocket = null // 重置 webSocket对象，为了可以重新获取 webSocket连接地址
                ConnectWebSocket()

            }, retryTime) // 等一定时间，再去重连webSocket

        }

    })

}

/**
 * 发送消息
 */
export function Send<T>(webSocketMessage: IWebSocketMessage<T>) {

    return WebSocketSend(myWebSocket, webSocketMessage);

}
