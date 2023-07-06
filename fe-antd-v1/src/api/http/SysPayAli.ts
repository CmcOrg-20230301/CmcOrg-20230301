import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

// 服务器异步通知，备注：第三方应用调用
export function SysPayAliNotifyCallBack(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sys/payAli/notifyCallBack', undefined, config)
}
