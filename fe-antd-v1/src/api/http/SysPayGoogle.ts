import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysPayGooglePaySuccessDTO {
    sysPayConfigurationId?: string // 支付配置主键 id，required：true，format：int64
    id?: string // 主键 id，required：true，format：int64
    token?: string // 购买成功后 Purchase对象的 getPurchaseToken()，required：true
}

// 支付成功的回调，备注：由客户端调用
export function SysPayGooglePaySuccess(form: SysPayGooglePaySuccessDTO, config?: AxiosRequestConfig) {
    return $http.myPost<boolean>('/sys/payGoogle/paySuccess', form, config)
}

export interface SysPayGooglePayConsumeDTO {
    sysPayConfigurationId?: string // 支付配置主键 id，required：true，format：int64
    id?: string // 主键 id，required：true，format：int64
}

// 支付核销的回调，备注：由客户端调用
export function SysPayGooglePayConsume(form: SysPayGooglePayConsumeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<boolean>('/sys/payGoogle/payConsume', form, config)
}
