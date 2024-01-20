import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysQrCodeSceneBindVO {
    sceneFlag?: boolean // 是否：已经扫码
    errorMsg?: string // 错误信息
}

// 设置密码：获取二维码是否已经被扫描
export function SignWxSetPasswordGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setPassword/getQrCodeSceneFlag', form, config)
}

export interface SignWxUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
    id?: string // 主键 id，required：true，format：int64
}

// 修改密码
export function SignWxUpdatePassword(form: SignWxUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updatePassword', form, config)
}

export interface UserSignBaseDTO {
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

export interface GetQrCodeVO {
    expireTs?: string // 二维码过期时间戳，format：int64
    qrCodeId?: string // 二维码 id，format：int64
    qrCodeUrl?: string // 二维码的 url地址
}

// 扫码登录：获取二维码
export function SignWxSignInGetQrCodeUrl(form: UserSignBaseDTO, config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/sign/in/getQrCodeUrl', form, config)
}

// 修改微信：获取新的二维码是否已经被扫描
export function SignWxUpdateWxGetQrCodeSceneFlagNew(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updateWx/getQrCodeSceneFlag/new', form, config)
}

// 设置登录名：获取二维码是否已经被扫描
export function SignWxSetSignInNameGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setSignInName/getQrCodeSceneFlag', form, config)
}

export interface SignWxUpdateEmailDTO {
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    id?: string // 主键 id，required：true，format：int64
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 修改邮箱
export function SignWxUpdateEmail(form: SignWxUpdateEmailDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updateEmail', form, config)
}

export interface SignWxSetEmailSendCodeDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 设置邮箱：发送验证码
export function SignWxSetEmailSendCode(form: SignWxSetEmailSendCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/setEmail/sendCode', form, config)
}

// 修改微信：获取新的二维码地址
export function SignWxUpdateWxGetQrCodeUrlNew(config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/updateWx/getQrCodeUrl/new', undefined, config)
}

export interface SignWxSetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
    id?: string // 主键 id，required：true，format：int64
}

// 设置密码
export function SignWxSetPassword(form: SignWxSetPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setPassword', form, config)
}

// 修改邮箱：获取二维码是否已经被扫描
export function SignWxUpdateEmailGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updateEmail/getQrCodeSceneFlag', form, config)
}

// 修改登录名：获取二维码是否已经被扫描
export function SignWxUpdateSignInNameGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updateSignInName/getQrCodeSceneFlag', form, config)
}

export interface SignInMiniProgramPhoneCodeDTO {
    appId?: string // 微信 appId，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    phoneCode?: string // 手机号码 code，required：true
}

export interface SignInVO {
    jwtExpireTs?: string // jwt过期时间戳，format：int64
    jwt?: string // jwt
    tenantId?: string // 租户主键 id，format：int64
}

// 小程序：手机号 code登录
export function SignWxSignInMiniProgramPhoneCode(form: SignInMiniProgramPhoneCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SignInVO>('/sign/wx/sign/in/miniProgram/phoneCode', form, config)
}

export interface SignInBrowserCodeDTO {
    code?: string // 微信 code，required：true
    appId?: string // 微信 appId，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

// 浏览器：微信 code登录
export function SignWxSignInBrowserCode(form: SignInBrowserCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SignInVO>('/sign/wx/sign/in/browser/code', form, config)
}

export interface SignWxSetPhoneDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    id?: string // 主键 id，required：true，format：int64
}

// 设置手机
export function SignWxSetPhone(form: SignWxSetPhoneDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setPhone', form, config)
}

// 修改微信：获取旧的二维码地址
export function SignWxUpdateWxGetQrCodeUrlOld(config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/updateWx/getQrCodeUrl/old', undefined, config)
}

export interface SignWxSetEmailGetQrCodeUrlDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 设置邮箱-获取二维码
export function SignWxSetEmailGetQrCodeUrl(form: SignWxSetEmailGetQrCodeUrlDTO, config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/setEmail/getQrCodeUrl', form, config)
}

// 扫码登录：通过二维码 id
export function SignWxSignInByQrCodeId(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SignInVO>('/sign/wx/sign/in/byQrCodeId', form, config)
}

// 账号注销-获取二维码
export function SignWxSignDeleteGetQrCodeUrl(config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/signDelete/getQrCodeUrl', undefined, config)
}

export interface SignWxUpdateSignInNameDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    id?: string // 主键 id，required：true，format：int64
}

// 修改登录名
export function SignWxUpdateSignInName(form: SignWxUpdateSignInNameDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updateSignInName', form, config)
}

// 设置密码-获取二维码
export function SignWxSetPasswordGetQrCodeUrl(config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/setPassword/getQrCodeUrl', undefined, config)
}

export interface SignWxUpdateSignInNameGetQrCodeUrlDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 修改登录名-获取二维码
export function SignWxUpdateSignInNameGetQrCodeUrl(form: SignWxUpdateSignInNameGetQrCodeUrlDTO, config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/updateSignInName/getQrCodeUrl', form, config)
}

export interface SignWxUpdateEmailGetQrCodeUrlDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 修改邮箱-获取二维码
export function SignWxUpdateEmailGetQrCodeUrl(form: SignWxUpdateEmailGetQrCodeUrlDTO, config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/updateEmail/getQrCodeUrl', form, config)
}

// 浏览器：微信 code登录，可以获取用户的基础信息
export function SignWxSignInBrowserCodeUserInfo(form: SignInBrowserCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SignInVO>('/sign/wx/sign/in/browser/code/userInfo', form, config)
}

export interface SignWxUpdateWxDTO {
    oldQrCodeId?: string // 旧的二维码 id，required：true，format：int64
    newQrCodeId?: string // 新的二维码 id，required：true，format：int64
}

// 修改微信
export function SignWxUpdateWx(form: SignWxUpdateWxDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updateWx', form, config)
}

// 修改密码-获取二维码
export function SignWxUpdatePasswordGetQrCodeUrl(config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/updatePassword/getQrCodeUrl', undefined, config)
}

// 账号注销
export function SignWxSignDelete(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/signDelete', form, config)
}

export interface SignWxUpdateEmailSendCodeDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 修改邮箱：发送验证码
export function SignWxUpdateEmailSendCode(form: SignWxUpdateEmailSendCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/updateEmail/sendCode', form, config)
}

export interface SignWxSetPhoneGetQrCodeUrlDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 设置手机：获取二维码
export function SignWxSetPhoneGetQrCodeUrl(form: SignWxSetPhoneGetQrCodeUrlDTO, config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/setPhone/getQrCodeUrl', form, config)
}

// 设置手机：获取二维码是否已经被扫描
export function SignWxSetPhoneGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setPhone/getQrCodeSceneFlag', form, config)
}

// 修改微信：获取旧的二维码是否已经被扫描
export function SignWxUpdateWxGetQrCodeSceneFlagOld(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updateWx/getQrCodeSceneFlag/old', form, config)
}

export interface SignWxSetEmailDTO {
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    id?: string // 主键 id，required：true，format：int64
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 设置邮箱
export function SignWxSetEmail(form: SignWxSetEmailDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setEmail', form, config)
}

export interface SignWxSetSignInNameDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    id?: string // 主键 id，required：true，format：int64
}

// 设置登录名
export function SignWxSetSignInName(form: SignWxSetSignInNameDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setSignInName', form, config)
}

// 设置邮箱：获取二维码是否已经被扫描
export function SignWxSetEmailGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/setEmail/getQrCodeSceneFlag', form, config)
}

export interface SignWxSetSignInNameGetQrCodeUrlDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 设置登录名-获取二维码
export function SignWxSetSignInNameGetQrCodeUrl(form: SignWxSetSignInNameGetQrCodeUrlDTO, config?: AxiosRequestConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/wx/setSignInName/getQrCodeUrl', form, config)
}

export interface SignWxSetPhoneSendCodeDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 设置手机：发送验证码
export function SignWxSetPhoneSendCode(form: SignWxSetPhoneSendCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sign/wx/setPhone/sendCode', form, config)
}

// 修改密码：获取二维码是否已经被扫描
export function SignWxUpdatePasswordGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/wx/updatePassword/getQrCodeSceneFlag', form, config)
}

export interface SignInMiniProgramCodeDTO {
    code?: string // 微信 code，required：true
    appId?: string // 微信 appId，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

// 小程序：微信 code登录
export function SignWxSignInMiniProgramCode(form: SignInMiniProgramCodeDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SignInVO>('/sign/wx/sign/in/miniProgram/code', form, config)
}
