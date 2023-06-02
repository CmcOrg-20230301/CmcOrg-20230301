import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

// 服务器异步通知-native，备注：第三方应用调用
export function SysPayWxNotifyCallBackNative(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sys/payWx/notifyCallBack/native', undefined, config)
}
