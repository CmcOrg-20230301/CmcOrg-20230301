import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullInteger {
    value?: number // 值，required：true，format：int32
}

export interface NettyWebSocketRegisterVO {
    code?: string // webSocket 连接码，备注：只能使用一次
    webSocketUrl?: string // webSocket 连接地址，ip:port
}

// 获取：webSocket连接地址和随机码
export function NettyWebSocketRegister(form: NotNullInteger, config?: AxiosRequestConfig) {
    return $http.myPost<NettyWebSocketRegisterVO>('/netty/webSocket/register', form, config)
}
