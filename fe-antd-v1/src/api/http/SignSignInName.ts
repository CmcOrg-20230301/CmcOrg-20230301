import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysQrCodeSceneBindVO {
    sceneFlag?: boolean // 是否：已经扫码
    errorMsg?: string // 错误信息
}

// 设置微信：获取二维码是否已经被扫描
export function SignSignInNameSetWxGetQrCodeSceneFlag(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/signInName/setWx/getQrCodeSceneFlag', form, config)
}

export interface GetQrCodeVO {
    expireTs?: string // 二维码过期时间戳，format：int64
    qrCodeId?: string // 二维码 id，format：int64
    qrCodeUrl?: string // 二维码的 url地址
}

// 设置统一登录：微信：获取统一登录微信的二维码地址
export function SignSignInNameSetSingleSignInWxGetQrCodeUrl(config?: IHttpConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/signInName/setSingleSignIn/wx/getQrCodeUrl', undefined, config)
}

export interface SignSignInNameSetWxDTO {
    id?: string // 主键 id，required：true，format：int64
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 设置微信
export function SignSignInNameSetWx(form: SignSignInNameSetWxDTO, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/signInName/setWx', form, config)
}

export interface SignSignInNameSetSingleSignInWxDTO {
    id?: string // 主键 id，required：true，format：int64
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 设置统一登录：微信
export function SignSignInNameSetSingleSignInWx(form: SignSignInNameSetSingleSignInWxDTO, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/signInName/setSingleSignIn/wx', form, config)
}

export interface SignSignInNameSetPhoneSendCodeDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 设置手机：发送验证码
export function SignSignInNameSetPhoneSendCode(form: SignSignInNameSetPhoneSendCodeDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/setPhone/sendCode', form, config)
}

export interface SignSignInNameSignDeleteDTO {
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 账号注销
export function SignSignInNameSignDelete(form: SignSignInNameSignDeleteDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/signDelete', form, config)
}

// 设置统一登录：微信：获取统一登录微信的二维码是否已经被扫描
export function SignSignInNameSetSingleSignInWxGetQrCodeSceneFlag(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/signInName/setSingleSignIn/wx/getQrCodeSceneFlag', form, config)
}

export interface SignSignInNameSetEmailDTO {
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 设置邮箱
export function SignSignInNameSetEmail(form: SignSignInNameSetEmailDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/setEmail', form, config)
}

export interface SignSignInNameSetSingleSignInPhoneDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 设置统一登录：手机验证码
export function SignSignInNameSetSingleSignInPhone(form: SignSignInNameSetSingleSignInPhoneDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/setSingleSignIn/phone', form, config)
}

export interface SignSignInNameUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    oldPassword?: string // 前端加密之后的旧密码，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 修改密码
export function SignSignInNameUpdatePassword(form: SignSignInNameUpdatePasswordDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/updatePassword', form, config)
}

export interface SignSignInNameSetSingleSignInPhoneSendCodeDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 设置统一登录：手机验证码：发送验证码
export function SignSignInNameSetSingleSignInPhoneSendCode(form: SignSignInNameSetSingleSignInPhoneSendCodeDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/setSingleSignIn/phone/sendCode', form, config)
}

export interface SignSignInNameSetEmailSendCodeDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 设置邮箱：发送验证码
export function SignSignInNameSetEmailSendCode(form: SignSignInNameSetEmailSendCodeDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/setEmail/sendCode', form, config)
}

export interface SignSignInNameUpdateSignInNameDTO {
    newSignInName?: string // 新登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 修改登录名
export function SignSignInNameUpdateSignInName(form: SignSignInNameUpdateSignInNameDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/updateSignInName', form, config)
}

export interface SignSignInNameSignUpDTO {
    password?: string // 前端加密之后的密码，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    originPassword?: string // 前端加密之后的原始密码，required：true
}

// 注册
export function SignSignInNameSignUp(form: SignSignInNameSignUpDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/sign/up', form, config)
}

export interface SignSignInNameSignInPasswordDTO {
    password?: string // 前端加密之后的密码，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

export interface SignInVO {
    jwtExpireTs?: string // jwt过期时间戳，format：int64
    jwt?: string // jwt
    tenantId?: string // 租户主键 id，format：int64
    jwtRefreshToken?: string // jwtRefreshToken
}

// 账号密码登录
export function SignSignInNameSignInPassword(form: SignSignInNameSignInPasswordDTO, config?: IHttpConfig) {
    return $http.myPost<SignInVO>('/sign/signInName/sign/in/password', form, config)
}

// 设置微信：获取二维码地址
export function SignSignInNameSetWxGetQrCodeUrl(config?: IHttpConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/signInName/setWx/getQrCodeUrl', undefined, config)
}

export interface SignSignInNameSetPhoneDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    currentPassword?: string // 前端加密之后的密码，required：true
}

// 设置手机
export function SignSignInNameSetPhone(form: SignSignInNameSetPhoneDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/signInName/setPhone', form, config)
}
