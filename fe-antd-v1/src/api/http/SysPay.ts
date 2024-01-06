import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

// 通过主键id，查看支付状态-第三方支付平台
export function SysPayPayTradeStatusByIdOther(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/pay/payTradeStatusById/other', form, config)
}

// 通过主键id，查看支付状态-本平台
export function SysPayPayTradeStatusById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/pay/payTradeStatusById', form, config)
}
