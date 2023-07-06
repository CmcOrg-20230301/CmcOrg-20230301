import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

// 当前用户-退出登录
export function SignOutSelf(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/out/self', undefined, config)
}
