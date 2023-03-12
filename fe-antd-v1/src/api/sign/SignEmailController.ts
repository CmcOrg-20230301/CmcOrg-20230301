import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SignEmailBindAccountDTO {

    code: string // 邮箱验证码 {"regexp":"^[0-9]{6}$"}
    email: string // 邮箱 {"sizeMax":200,"sizeMin":0,"regexp":"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"}

}

// 绑定邮箱
export function SignEmailBindAccount(form: SignEmailBindAccountDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/bindAccount', form, config)

}

export interface EmailNotBlankDTO {

    email: string // 邮箱 {"sizeMax":200,"sizeMin":0,"regexp":"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"}

}

// 绑定邮箱-发送验证码
export function SignEmailBindAccountSendCode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/bindAccount/sendCode', form, config)

}

export interface SignEmailForgetPasswordDTO {

    code: string // 邮箱验证码 {"regexp":"^[0-9]{6}$"}
    newPassword: string // 前端加密之后的新密码
    originNewPassword: string // 前端加密之后的原始新密码
    email: string // 邮箱 {"sizeMax":200,"sizeMin":0,"regexp":"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"}

}

// 忘记密码
export function SignEmailForgetPassword(form: SignEmailForgetPasswordDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/forgetPassword', form, config)

}

// 忘记密码-发送验证码
export function SignEmailForgetPasswordSendCode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/forgetPassword/sendCode', form, config)

}

export interface NotBlankCodeDTO {

    code: string // 验证码 {"regexp":"^[0-9]{6}$"}

}

// 账号注销
export function SignEmailSignDelete(form: NotBlankCodeDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/signDelete', form, config)

}

// 账号注销-发送验证码
export function SignEmailSignDeleteSendCode(config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/signDelete/sendCode', undefined, config)

}

export interface SignEmailSignInPasswordDTO {

    password: string // 密码
    email: string // 邮箱 {"sizeMax":200,"sizeMin":0,"regexp":"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"}

}

// 邮箱账号密码登录
export function SignEmailSignInPassword(form: SignEmailSignInPasswordDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/sign/in/password', form, config)

}

export interface SignEmailSignUpDTO {

    code: string // 邮箱验证码 {"regexp":"^[0-9]{6}$"}
    password: string // 前端加密之后的密码
    originPassword: string // 前端加密之后的原始密码
    email: string // 邮箱 {"sizeMax":200,"sizeMin":0,"regexp":"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"}

}

// 注册
export function SignEmailSignUp(form: SignEmailSignUpDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/sign/up', form, config)

}

// 注册-发送验证码
export function SignEmailSignUpSendCode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/sign/up/sendCode', form, config)

}

export interface SignEmailUpdateAccountDTO {

    newEmail: string // 新邮箱 {"sizeMax":200,"sizeMin":0,"regexp":"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"}
    oldEmailCode: string // 旧邮箱验证码 {"regexp":"^[0-9]{6}$"}
    newEmailCode: string // 新邮箱验证码 {"regexp":"^[0-9]{6}$"}

}

// 修改邮箱
export function SignEmailUpdateAccount(form: SignEmailUpdateAccountDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/updateAccount', form, config)

}

// 修改邮箱-发送验证码
export function SignEmailUpdateAccountSendCode(config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/updateAccount/sendCode', undefined, config)

}

export interface SignEmailUpdatePasswordDTO {

    code: string // 邮箱验证码 {"regexp":"^[0-9]{6}$"}
    newPassword: string // 前端加密之后的新密码
    originNewPassword: string // 前端加密之后的原始新密码

}

// 修改密码
export function SignEmailUpdatePassword(form: SignEmailUpdatePasswordDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/updatePassword', form, config)

}

// 修改密码-发送验证码
export function SignEmailUpdatePasswordSendCode(config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sign/email/updatePassword/sendCode', undefined, config)

}
