import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface SignSingleSignInSendCodePhoneDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 统一登录：手机验证码登录：发送验证码
export function SignSingleSignInSendCodePhone(form: SignSingleSignInSendCodePhoneDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/single/sign/in/sendCode/phone', form, config)
}

export interface GetQrCodeVO {
    expireTs?: string // 二维码过期时间戳，format：int64
    qrCodeId?: string // 二维码 id，format：int64
    qrCodeUrl?: string // 二维码的 url地址
}

// 统一登录：微信扫码登录：获取二维码
export function SignSingleSignInGetQrCodeUrlWx(config?: IHttpConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/single/sign/in/getQrCodeUrl/wx', undefined, config)
}

export interface SysSignConfigurationVO {
    emailSignUpEnable?: boolean // 是否启用：邮箱注册功能，默认启用
    signInNameSignUpEnable?: boolean // 是否启用：用户名注册功能，默认启用
    wxQrCodeSignUp?: GetQrCodeVO // null
    phoneSignUpEnable?: boolean // 是否启用：手机号码注册功能，默认启用
}

// 获取：统一登录相关的配置
export function SignSingleSignInGetConfiguration(config?: IHttpConfig) {
    return $http.myPost<SysSignConfigurationVO>('/sign/single/sign/in/getConfiguration', undefined, config)
}

export interface SignSingleSignInCodePhoneDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

export interface SignInVO {
    jwtExpireTs?: string // jwt过期时间戳，format：int64
    jwt?: string // jwt
    tenantId?: string // 租户主键 id，format：int64
}

// 统一登录：手机验证码登录
export function SignSingleSignInCodePhone(form: SignSingleSignInCodePhoneDTO, config?: IHttpConfig) {
    return $http.myPost<SignInVO>('/sign/single/sign/in/code/phone', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

// 统一登录：微信扫码登录：通过二维码 id
export function SignSingleSignInByQrCodeIdWx(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<SignInVO>('/sign/single/sign/in/byQrCodeId/wx', form, config)
}
