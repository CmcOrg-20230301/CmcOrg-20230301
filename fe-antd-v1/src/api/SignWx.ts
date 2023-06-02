import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SignInMiniProgramPhoneCodeDTO {
    phoneCode?: string // 手机号码 code，required：true
}

// 小程序：手机号 code登录
export function SignWxSignInMiniProgramPhoneCode(form: SignInMiniProgramPhoneCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/sign/in/miniProgram/phoneCode', form, config)
}

export interface SignInBrowserCodeDTO {
    code?: string // 微信 code，required：true
}

// 浏览器：微信 code登录
export function SignWxSignInBrowserCode(form: SignInBrowserCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/sign/in/browser/code', form, config)
}

export interface SignInMiniProgramCodeDTO {
    code?: string // 微信 code，required：true
}

// 小程序：微信 code登录
export function SignWxSignInMiniProgramCode(form: SignInMiniProgramCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/sign/in/miniProgram/code', form, config)
}
