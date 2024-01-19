import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SignEmailSetSignInNameDTO {
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 修改登录名
export function SignEmailUpdateSignInName(form: SignEmailSetSignInNameDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/updateSignInName', form, config)
}

export interface SignEmailSignUpDTO {
    password?: string // 前端加密之后的密码，required：true
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
    originPassword?: string // 前端加密之后的原始密码，required：true
}

// 注册
export function SignEmailSignUp(form: SignEmailSignUpDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/sign/up', form, config)
}

export interface EmailNotBlankDTO {
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 注册-发送验证码
export function SignEmailSignUpSendCode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/sign/up/sendCode', form, config)
}

export interface SignEmailUpdateEmailSendCodeNewDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 修改邮箱-发送新邮箱验证码
export function SignEmailUpdateEmailSendCodeNew(form: SignEmailUpdateEmailSendCodeNewDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/updateEmail/sendCode/new', form, config)
}

// 修改邮箱-发送旧邮箱验证码
export function SignEmailUpdateEmailSendCodeOld(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/updateEmail/sendCode/old', undefined, config)
}

export interface SignEmailUpdateSignInNameSendCodeDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 修改登录名-发送验证码
export function SignEmailUpdateSignInNameSendCode(form: SignEmailUpdateSignInNameSendCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/updateSignInName/sendCode', form, config)
}

export interface SignEmailForgetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    newPassword?: string // 前端加密之后的新密码，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 忘记密码
export function SignEmailForgetPassword(form: SignEmailForgetPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/forgetPassword', form, config)
}

// 忘记密码-发送验证码
export function SignEmailForgetPasswordSendCode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/forgetPassword/sendCode', form, config)
}

// 修改密码-发送验证码
export function SignEmailUpdatePasswordSendCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/updatePassword/sendCode', undefined, config)
}

export interface SignEmailSetPhoneSendCodePhoneDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 设置手机：发送手机验证码
export function SignEmailSetPhoneSendCodePhone(form: SignEmailSetPhoneSendCodePhoneDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/setPhone/sendCode/phone', form, config)
}

// 账号注销-发送验证码
export function SignEmailSignDeleteSendCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/signDelete/sendCode', undefined, config)
}

export interface SignEmailSetSignInNameSendCodeDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 设置登录名-发送验证码
export function SignEmailSetSignInNameSendCode(form: SignEmailSetSignInNameSendCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/setSignInName/sendCode', form, config)
}

export interface GetQrCodeVO {
    expireTs?: string // 二维码过期时间戳，format：int64
    qrCodeId?: string // 二维码 id，format：int64
    qrCodeUrl?: string // 二维码的 url地址
}

// 设置微信：获取二维码地址
export function SignEmailSetWxGetQrCodeUrl(config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/email/setWx/getQrCodeUrl', undefined, config)
}

// 设置登录名
export function SignEmailSetSignInName(form: SignEmailSetSignInNameDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/setSignInName', form, config)
}

export interface SignEmailSetWxDTO {
    qrCodeId?: string // 二维码 id，required：true，format：int64
    emailCode?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
}

export interface SysQrCodeSceneBindVO {
    sceneFlag?: boolean // 是否：已经扫码
    errorMsg?: string // 错误信息
}

// 设置微信
export function SignEmailSetWx(form: SignEmailSetWxDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/email/setWx', form, config)
}

// 设置微信：发送验证码
export function SignEmailSetWxSendCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/setWx/sendCode', undefined, config)
}

export interface SignEmailUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 修改密码
export function SignEmailUpdatePassword(form: SignEmailUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/updatePassword', form, config)
}

export interface SignEmailUpdateEmailDTO {
    newEmailCode?: string // 新邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    newEmail?: string // 新邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
    oldEmailCode?: string // 旧邮箱验证码，正则表达式：^[0-9]{6}$，required：true
}

// 修改邮箱
export function SignEmailUpdateEmail(form: SignEmailUpdateEmailDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/updateEmail', form, config)
}

// 设置手机：发送邮箱验证码
export function SignEmailSetPhoneSendCodeEmail(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/setPhone/sendCode/email', undefined, config)
}

export interface SignEmailSetPhoneDTO {
    emailCode?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    phoneCode?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
}

// 设置手机
export function SignEmailSetPhone(form: SignEmailSetPhoneDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/setPhone', form, config)
}

export interface NotBlankCodeDTO {
    code?: string // 验证码，正则表达式：^[0-9]{6}$，required：true
}

// 账号注销
export function SignEmailSignDelete(form: NotBlankCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/email/signDelete', form, config)
}

export interface SignEmailSignInPasswordDTO {
    password?: string // 密码，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

export interface SignInVO {
    jwtExpireTs?: string // jwt过期时间戳，format：int64
    jwt?: string // jwt
    tenantId?: string // 租户主键 id，format：int64
}

// 邮箱：账号密码登录
export function SignEmailSignInPassword(form: SignEmailSignInPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SignInVO>('/sign/email/sign/in/password', form, config)
}
