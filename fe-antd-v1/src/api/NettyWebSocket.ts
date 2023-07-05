import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullInteger {
    value?: number // 值，required：true，format：int32
}

export interface NettyWebSocketRegisterVO {
    webSocketUrl?: string // webSocket 连接地址，ip:port/path?code=xxx
}

// 获取：webSocket连接地址和随机码
export function NettyWebSocketRegister(form: NotNullInteger, config?: AxiosRequestConfig) {
    return $http.myPost<NettyWebSocketRegisterVO>('/netty/webSocket/register', form, config)
}
