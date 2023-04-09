import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SignSignInNameUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    oldPassword?: string // 前端加密之后的旧密码，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 修改密码
export function SignSigninnameUpdatepassword(form: SignSignInNameUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/signInName/updatePassword', form, config)
}

export interface SignSignInNameSignDeleteDTO {
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 账号注销
export function SignSigninnameSigndelete(form: SignSignInNameSignDeleteDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/signInName/signDelete', form, config)
}

export interface SignSignInNameSignUpDTO {
    password?: string // 前端加密之后的密码，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    originPassword?: string // 前端加密之后的原始密码，required：true
}

// 注册
export function SignSigninnameSignUp(form: SignSignInNameSignUpDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/signInName/sign/up', form, config)
}

export interface SignSignInNameUpdateAccountDTO {
    newSignInName?: string // 新登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 修改账号
export function SignSigninnameUpdateaccount(form: SignSignInNameUpdateAccountDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/signInName/updateAccount', form, config)
}

export interface SignSignInNameSignInPasswordDTO {
    password?: string // 前端加密之后的密码，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 账号密码登录
export function SignSigninnameSignInPassword(form: SignSignInNameSignInPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/signInName/sign/in/password', form, config)
}
