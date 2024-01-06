import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullIdAndIntegerValue {
    id?: string // 主键 id，required：true，format：int64
    value?: number // 值，required：true，format：int32
}

// 通过主键 id，获取：webSocket连接地址，格式：scheme://ip:port/path?code=xxx
export function NettyWebSocketGetWebSocketUrlById(form: NotNullIdAndIntegerValue, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/netty/webSocket/getWebSocketUrlById', form, config)
}

// 获取：所有 webSocket连接地址，格式：scheme://ip:port/path?code=xxx
export function NettyWebSocketGetAllWebSocketUrl(config?: AxiosRequestConfig) {
    return $http.myPost<string[]>('/netty/webSocket/getAllWebSocketUrl', undefined, config)
}

// 心跳检测
export function NettyWebSocketHeartBeat(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/netty/webSocket/heartBeat', undefined, config)
}
