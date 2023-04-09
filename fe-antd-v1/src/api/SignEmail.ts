import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SignEmailBindAccountDTO {
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 绑定邮箱
export function SignEmailBindaccount(form: SignEmailBindAccountDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/bindAccount', form, config)
}

export interface SignEmailSignUpDTO {
    password?: string // 前端加密之后的密码，required：true
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
    originPassword?: string // 前端加密之后的原始密码，required：true
}

// 注册
export function SignEmailSignUp(form: SignEmailSignUpDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/sign/up', form, config)
}

export interface EmailNotBlankDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 注册-发送验证码
export function SignEmailSignUpSendcode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/sign/up/sendCode', form, config)
}

// 绑定邮箱-发送验证码
export function SignEmailBindaccountSendcode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/bindAccount/sendCode', form, config)
}

export interface SignEmailForgetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 忘记密码
export function SignEmailForgetpassword(form: SignEmailForgetPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/forgetPassword', form, config)
}

// 忘记密码-发送验证码
export function SignEmailForgetpasswordSendcode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/forgetPassword/sendCode', form, config)
}

// 修改密码-发送验证码
export function SignEmailUpdatepasswordSendcode(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/updatePassword/sendCode', undefined, config)
}

// 修改邮箱-发送验证码
export function SignEmailUpdateaccountSendcode(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/updateAccount/sendCode', undefined, config)
}

// 账号注销-发送验证码
export function SignEmailSigndeleteSendcode(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/signDelete/sendCode', undefined, config)
}

export interface SignEmailUpdateAccountDTO {
    newEmailCode?: string // 新邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    newEmail?: string // 新邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
    oldEmailCode?: string // 旧邮箱验证码，正则表达式：^[0-9]{6}$，required：true
}

// 修改邮箱
export function SignEmailUpdateaccount(form: SignEmailUpdateAccountDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/updateAccount', form, config)
}

export interface SignEmailUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 修改密码
export function SignEmailUpdatepassword(form: SignEmailUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/updatePassword', form, config)
}

export interface NotBlankCodeDTO {
    code?: string // 验证码，正则表达式：^[0-9]{6}$，required：true
}

// 账号注销
export function SignEmailSigndelete(form: NotBlankCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/signDelete', form, config)
}

export interface SignEmailSignInPasswordDTO {
    password?: string // 密码，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 邮箱账号密码登录
export function SignEmailSignInPassword(form: SignEmailSignInPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/email/sign/in/password', form, config)
}
