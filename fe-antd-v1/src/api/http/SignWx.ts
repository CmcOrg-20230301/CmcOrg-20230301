import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SignInBrowserCodeDTO {
    code?: string // 微信 code，required：true
    appId?: string // 微信 appId，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

// 浏览器：微信 code登录，可以获取用户的基础信息
export function SignWxSignInBrowserCodeUserInfo(form: SignInBrowserCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/sign/in/browser/code/userInfo', form, config)
}

export interface SignInMiniProgramPhoneCodeDTO {
    appId?: string // 微信 appId，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    phoneCode?: string // 手机号码 code，required：true
}

// 小程序：手机号 code登录
export function SignWxSignInMiniProgramPhoneCode(form: SignInMiniProgramPhoneCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/sign/in/miniProgram/phoneCode', form, config)
}

// 浏览器：微信 code登录
export function SignWxSignInBrowserCode(form: SignInBrowserCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/sign/in/browser/code', form, config)
}

export interface SignInMiniProgramCodeDTO {
    code?: string // 微信 code，required：true
    appId?: string // 微信 appId，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

// 小程序：微信 code登录
export function SignWxSignInMiniProgramCode(form: SignInMiniProgramCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/sign/in/miniProgram/code', form, config)
}
