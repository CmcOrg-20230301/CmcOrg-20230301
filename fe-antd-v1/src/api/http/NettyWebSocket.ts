import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

// 获取：所有 webSocket连接地址，格式：scheme://ip:port/path?code=xxx
export function NettyWebSocketGetAllWebSocketUrl(config?: AxiosRequestConfig) {
    return $http.myPost<string[]>('/netty/webSocket/getAllWebSocketUrl', undefined, config)
}

// 心跳检测
export function NettyWebSocketHeartBeat(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/netty/webSocket/heartBeat', undefined, config)
}
