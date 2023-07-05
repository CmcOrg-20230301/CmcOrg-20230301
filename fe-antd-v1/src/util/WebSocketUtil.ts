import {NettyWebSocketRegister} from "@/api/NettyWebSocket";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {getAppDispatch} from "@/MyApp";
import {setWebSocketMessage, setWebSocketStatus} from "@/store/commonSlice";

let webSocketUrl: string | undefined = ''
let webSocket: WebSocket | null = null

// 备注：开发环境的超时时间设置长一点
const retryTime = import.meta.env.DEV ? 5000 : 2000

// 获取：webSocket的连接信息
function GetWebSocketRegisterData() {

    webSocketUrl = ''

    return NettyWebSocketRegister(
        {
            value: 1
        },
        {
            timeout: retryTime,
            headers: {
                hiddenErrorMsg: true,
            },
        } as any
    )
        .then(({data}) => {

            webSocketUrl = data.webSocketUrl

        })
        .catch(async () => {

            if (!localStorage.getItem(LocalStorageKey.JWT)) {
                return // 如果没有 jwt，则不重连了，目的：防止一直连
            }

            await new Promise((resolve) => {

                setTimeout(async () => {

                    await GetWebSocketRegisterData() // 等一定时间，再次获取 webSocket服务器
                    resolve(null)

                }, retryTime)

            })

        })
}

// 关闭 webSocket
export function CloseWebSocket() {

    if (webSocket) {

        webSocket.close()
        webSocket = null

    }

}

export const WebSocketMap = new Map<string, <T>(data: IWebSocketMessage<T>) => void>();

export interface IWebSocketMessage<T> {
    path: string // 路径
    data?: T // 数据
}

// 连接 webSocket
export function ConnectWebSocket() {

    if (webSocket) {
        return
    }

    if (!localStorage.getItem(LocalStorageKey.JWT)) {
        return // 如果没有 jwt，则不重连了，目的：防止一直连
    }

    return new Promise(async (resolve, reject) => {

            if (!window.WebSocket) {
                return reject(new Error('您的浏览器不支持 WebSocket协议，请更换浏览器再试'))
            }

            await GetWebSocketRegisterData()

            if (!webSocketUrl) {
                return reject(new Error('连接 WebSocket失败：暂无可用的服务器'))
            }

            if (webSocket) {
                return
            }

            webSocket = new WebSocket(
                `${import.meta.env.DEV ? 'ws:' : 'wss:'}//${webSocketUrl}`
            )

            webSocket.onmessage = (message: MessageEvent<string>) => {

                const webSocketMessage: IWebSocketMessage<any> = JSON.parse(message.data)

                // 更新 redux里面 webSocket的值
                getAppDispatch()(setWebSocketMessage({} as IWebSocketMessage<any>)) // 先重置，再设置值

                setTimeout(() => {
                    getAppDispatch()(setWebSocketMessage(webSocketMessage))
                }, 200)

            }

            webSocket.onopen = (event) => {

                console.log(`WebSocket 连接 >> ${webSocketUrl?.substring(0, webSocketUrl!.indexOf("?"))}`)

                getAppDispatch()(setWebSocketStatus(true))

            }

            webSocket.onclose = (event) => {

                console.log('WebSocket 关闭')

                getAppDispatch()(setWebSocketStatus(false))

                setTimeout(() => {

                    webSocket = null // 重置 webSocket对象，为了可以重新获取 webSocket连接地址
                    ConnectWebSocket()

                }, retryTime) // 等一定时间，再去重连webSocket

            }

        }
    )
}
