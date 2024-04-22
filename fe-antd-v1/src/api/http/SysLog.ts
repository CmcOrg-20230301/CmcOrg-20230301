import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface SysLogPushDTO {
    log?: string // 日志，required：true
}

// 新增：日志记录
export function SysLogPush(form: SysLogPushDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/log/push', form, config)
}
