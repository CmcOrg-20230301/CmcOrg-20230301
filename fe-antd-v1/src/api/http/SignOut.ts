import {$http, IHttpConfig} from "@/util/HttpUtil";

// 当前用户-退出登录
export function SignOutSelf(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/out/self', undefined, config)
}
