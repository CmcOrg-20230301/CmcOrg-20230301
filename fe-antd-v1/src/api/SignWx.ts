import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SignInCodeDTO {
    code?: string // 微信 code，required：true
}

// 微信 code登录
export function SignWxSignInCode(form: SignInCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/wx/sign/in/code', form, config)
}

export interface SignInPhoneCodeDTO {
    phoneCode?: string // 手机号码 code，required：true
}

// 手机号 code登录
export function SignWxSignInPhonecode(form: SignInPhoneCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/wx/sign/in/phoneCode', form, config)
}
