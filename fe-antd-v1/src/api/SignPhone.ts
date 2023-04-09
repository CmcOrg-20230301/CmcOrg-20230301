import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface PhoneNotBlankDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 绑定手机-发送验证码
export function SignPhoneBindaccountSendcode(form: PhoneNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/bindAccount/sendCode', form, config)
}

export interface SignPhoneBindAccountDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 绑定手机
export function SignPhoneBindaccount(form: SignPhoneBindAccountDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/bindAccount', form, config)
}

// 手机验证码登录-发送验证码
export function SignPhoneSignInSendcode(form: PhoneNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/sign/in/sendCode', form, config)
}

// 账号注销-发送验证码
export function SignPhoneSigndeleteSendcode(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/signDelete/sendCode', undefined, config)
}

// 注册-发送验证码
export function SignPhoneSignUpSendcode(form: PhoneNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/sign/up/sendCode', form, config)
}

// 修改手机-发送验证码
export function SignPhoneUpdateaccountSendcode(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/updateAccount/sendCode', undefined, config)
}

export interface SignPhoneSignInCodeDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 手机验证码登录
export function SignPhoneSignInCode(form: SignPhoneSignInCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/sign/in/code', form, config)
}

// 修改密码-发送验证码
export function SignPhoneUpdatepasswordSendcode(config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/updatePassword/sendCode', undefined, config)
}

export interface SignPhoneForgetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 忘记密码
export function SignPhoneForgetpassword(form: SignPhoneForgetPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/forgetPassword', form, config)
}

export interface SignPhoneUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 修改密码
export function SignPhoneUpdatepassword(form: SignPhoneUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/updatePassword', form, config)
}

export interface SignPhoneSignUpDTO {
    password?: string // 前端加密之后的密码，required：true
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    originPassword?: string // 前端加密之后的原始密码，required：true
}

// 注册
export function SignPhoneSignUp(form: SignPhoneSignUpDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/sign/up', form, config)
}

// 忘记密码-发送验证码
export function SignPhoneForgetpasswordSendcode(form: PhoneNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/forgetPassword/sendCode', form, config)
}

export interface SignPhoneUpdateAccountDTO {
    newPhone?: string // 新手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    oldPhoneCode?: string // 旧手机验证码，正则表达式：^[0-9]{6}$，required：true
    newPhoneCode?: string // 新手机验证码，正则表达式：^[0-9]{6}$，required：true
}

// 修改手机
export function SignPhoneUpdateaccount(form: SignPhoneUpdateAccountDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/updateAccount', form, config)
}

export interface NotBlankCodeDTO {
    code?: string // 验证码，正则表达式：^[0-9]{6}$，required：true
}

// 账号注销
export function SignPhoneSigndelete(form: NotBlankCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/signDelete', form, config)
}

export interface SignPhoneSignInPasswordDTO {
    password?: string // 密码，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 手机账号密码登录
export function SignPhoneSignInPassword(form: SignPhoneSignInPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sign/phone/sign/in/password', form, config)
}
