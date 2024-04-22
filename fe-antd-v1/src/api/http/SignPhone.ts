import {$http, IHttpConfig} from "@/util/HttpUtil";

// 账号注销-发送验证码
export function SignPhoneSignDeleteSendCode(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/signDelete/sendCode', undefined, config)
}

// 设置密码-发送验证码
export function SignPhoneSetPasswordSendCode(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setPassword/sendCode', undefined, config)
}

export interface SignPhoneUpdateSignInNameSendCodeDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 修改登录名-发送验证码
export function SignPhoneUpdateSignInNameSendCode(form: SignPhoneUpdateSignInNameSendCodeDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updateSignInName/sendCode', form, config)
}

export interface GetQrCodeVO {
    expireTs?: string // 二维码过期时间戳，format：int64
    qrCodeId?: string // 二维码 id，format：int64
    qrCodeUrl?: string // 二维码的 url地址
}

// 设置微信：获取二维码地址
export function SignPhoneSetWxGetQrCodeUrl(config?: IHttpConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/phone/setWx/getQrCodeUrl', undefined, config)
}

export interface SignPhoneSignInCodeDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

export interface SignInVO {
    jwtExpireTs?: string // jwt过期时间戳，format：int64
    jwt?: string // jwt
    tenantId?: string // 租户主键 id，format：int64
    jwtRefreshToken?: string // jwtRefreshToken
}

// 手机验证码登录
export function SignPhoneSignInCode(form: SignPhoneSignInCodeDTO, config?: IHttpConfig) {
    return $http.myPost<SignInVO>('/sign/phone/sign/in/code', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysQrCodeSceneBindVO {
    sceneFlag?: boolean // 是否：已经扫码
    errorMsg?: string // 错误信息
}

// 修改微信：获取新微信二维码是否已经被扫描
export function SignPhoneUpdateWxGetQrCodeSceneFlagNew(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/phone/updateWx/getQrCodeSceneFlag/new', form, config)
}

// 修改微信：获取新微信的二维码地址
export function SignPhoneUpdateWxGetQrCodeUrlNew(config?: IHttpConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/phone/updateWx/getQrCodeUrl/new', undefined, config)
}

export interface SignPhoneUpdateEmailSendCodePhoneDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 修改邮箱-发送手机验证码
export function SignPhoneUpdateEmailSendCodePhone(form: SignPhoneUpdateEmailSendCodePhoneDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updateEmail/sendCode/phone', form, config)
}

// 修改手机-发送旧手机验证码
export function SignPhoneUpdatePhoneSendCodeOld(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updatePhone/sendCode/old', undefined, config)
}

export interface SignPhoneSetSingleSignInPhoneSendCodeDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 设置统一登录：手机验证码：发送要绑定统一登录手机的验证码
export function SignPhoneSetSingleSignInPhoneSendCode(form: SignPhoneSetSingleSignInPhoneSendCodeDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setSingleSignIn/phone/sendCode', form, config)
}

// 设置统一登录：微信：获取统一登录微信的二维码是否已经被扫描
export function SignPhoneSetSingleSignInWxGetQrCodeSceneFlag(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/phone/setSingleSignIn/wx/getQrCodeSceneFlag', form, config)
}

export interface SignPhoneSignUpDTO {
    password?: string // 前端加密之后的密码，required：true
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    originPassword?: string // 前端加密之后的原始密码，required：true
}

// 注册
export function SignPhoneSignUp(form: SignPhoneSignUpDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/sign/up', form, config)
}

// 修改微信：发送手机验证码
export function SignPhoneUpdateWxSendCodePhone(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updateWx/sendCode/phone', undefined, config)
}

export interface SignPhoneSetSingleSignInWxDTO {
    qrCodeId?: string // 二维码 id，required：true，format：int64
    phoneCode?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
}

// 设置统一登录：微信
export function SignPhoneSetSingleSignInWx(form: SignPhoneSetSingleSignInWxDTO, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/phone/setSingleSignIn/wx', form, config)
}

export interface SignPhoneUpdatePhoneSendCodeNewDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
}

// 修改手机-发送新手机验证码
export function SignPhoneUpdatePhoneSendCodeNew(form: SignPhoneUpdatePhoneSendCodeNewDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updatePhone/sendCode/new', form, config)
}

export interface SignPhoneUpdateWxDTO {
    qrCodeId?: string // 二维码 id，required：true，format：int64
    phoneCode?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
}

// 修改微信
export function SignPhoneUpdateWx(form: SignPhoneUpdateWxDTO, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/phone/updateWx', form, config)
}

// 设置统一登录：手机验证码：发送当前账号已经绑定手机的验证码
export function SignPhoneSetSingleSignInPhoneSendCodeCurrent(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setSingleSignIn/phone/sendCode/current', undefined, config)
}

export interface PhoneNotBlankDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

// 手机验证码登录-发送验证码
export function SignPhoneSignInSendCode(form: PhoneNotBlankDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/sign/in/sendCode', form, config)
}

// 设置微信-发送手机验证码
export function SignPhoneSetWxSendCodePhone(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setWx/sendCode/phone', undefined, config)
}

export interface SignPhoneUpdateSignInNameDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 修改登录名
export function SignPhoneUpdateSignInName(form: SignPhoneUpdateSignInNameDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updateSignInName', form, config)
}

// 注册-发送验证码
export function SignPhoneSignUpSendCode(form: PhoneNotBlankDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/sign/up/sendCode', form, config)
}

export interface SignPhoneSetWxDTO {
    qrCodeId?: string // 二维码 id，required：true，format：int64
    phoneCode?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
}

// 设置微信
export function SignPhoneSetWx(form: SignPhoneSetWxDTO, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/phone/setWx', form, config)
}

export interface SignPhoneSetEmailSendCodeEmailDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 设置邮箱-发送邮箱验证码
export function SignPhoneSetEmailSendCodeEmail(form: SignPhoneSetEmailSendCodeEmailDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setEmail/sendCode/email', form, config)
}

// 修改密码-发送验证码
export function SignPhoneUpdatePasswordSendCode(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updatePassword/sendCode', undefined, config)
}

export interface SignPhoneSetSingleSignInPhoneDTO {
    singleSignInPhone?: string // 统一登录的手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    singleSignInPhoneCode?: string // 统一登录的手机验证码，正则表达式：^[0-9]{6}$，required：true
    currentPhoneCode?: string // 账号已经绑定手机的验证码，正则表达式：^[0-9]{6}$，required：true
}

// 设置统一登录：手机验证码
export function SignPhoneSetSingleSignInPhone(form: SignPhoneSetSingleSignInPhoneDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setSingleSignIn/phone', form, config)
}

export interface SignPhoneSetSignInNameSendCodeDTO {
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 设置登录名-发送验证码
export function SignPhoneSetSignInNameSendCode(form: SignPhoneSetSignInNameSendCodeDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setSignInName/sendCode', form, config)
}

export interface SignPhoneSetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 设置密码
export function SignPhoneSetPassword(form: SignPhoneSetPasswordDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setPassword', form, config)
}

export interface SignPhoneUpdateEmailSendCodeEmailDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 修改邮箱-发送邮箱验证码
export function SignPhoneUpdateEmailSendCodeEmail(form: SignPhoneUpdateEmailSendCodeEmailDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updateEmail/sendCode/email', form, config)
}

export interface SignPhoneForgetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 忘记密码
export function SignPhoneForgetPassword(form: SignPhoneForgetPasswordDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/forgetPassword', form, config)
}

export interface SignPhoneUpdatePhoneDTO {
    newPhone?: string // 新手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    oldPhoneCode?: string // 旧手机验证码，正则表达式：^[0-9]{6}$，required：true
    newPhoneCode?: string // 新手机验证码，正则表达式：^[0-9]{6}$，required：true
}

// 修改手机
export function SignPhoneUpdatePhone(form: SignPhoneUpdatePhoneDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updatePhone', form, config)
}

// 设置微信：获取二维码是否已经被扫描
export function SignPhoneSetWxGetQrCodeSceneFlag(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<SysQrCodeSceneBindVO>('/sign/phone/setWx/getQrCodeSceneFlag', form, config)
}

export interface SignPhoneUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

// 修改密码
export function SignPhoneUpdatePassword(form: SignPhoneUpdatePasswordDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updatePassword', form, config)
}

export interface SignPhoneSetEmailDTO {
    emailCode?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    phoneCode?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 设置邮箱
export function SignPhoneSetEmail(form: SignPhoneSetEmailDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setEmail', form, config)
}

export interface SignPhoneSetEmailSendCodePhoneDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 设置邮箱-发送手机验证码
export function SignPhoneSetEmailSendCodePhone(form: SignPhoneSetEmailSendCodePhoneDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setEmail/sendCode/phone', form, config)
}

export interface SignPhoneUpdateEmailDTO {
    emailCode?: string // 邮箱验证码，正则表达式：^[0-9]{6}$，required：true
    phoneCode?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0，required：true
}

// 修改邮箱
export function SignPhoneUpdateEmail(form: SignPhoneUpdateEmailDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/updateEmail', form, config)
}

// 设置统一登录：微信：发送手机验证码
export function SignPhoneSetSingleSignInWxSendCode(config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setSingleSignIn/wx/sendCode', undefined, config)
}

export interface SignPhoneSetSignInNameDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}

// 设置登录名
export function SignPhoneSetSignInName(form: SignPhoneSetSignInNameDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/setSignInName', form, config)
}

// 忘记密码-发送验证码
export function SignPhoneForgetPasswordSendCode(form: PhoneNotBlankDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/forgetPassword/sendCode', form, config)
}

// 设置统一登录：微信：获取统一登录微信的二维码地址
export function SignPhoneSetSingleSignInWxGetQrCodeUrl(config?: IHttpConfig) {
    return $http.myPost<GetQrCodeVO>('/sign/phone/setSingleSignIn/wx/getQrCodeUrl', undefined, config)
}

export interface NotBlankCodeDTO {
    code?: string // 验证码，正则表达式：^[0-9]{6}$，required：true
}

// 账号注销
export function SignPhoneSignDelete(form: NotBlankCodeDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sign/phone/signDelete', form, config)
}

export interface SignPhoneSignInPasswordDTO {
    password?: string // 密码，required：true
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

// 手机：账号密码登录
export function SignPhoneSignInPassword(form: SignPhoneSignInPasswordDTO, config?: IHttpConfig) {
    return $http.myPost<SignInVO>('/sign/phone/sign/in/password', form, config)
}
