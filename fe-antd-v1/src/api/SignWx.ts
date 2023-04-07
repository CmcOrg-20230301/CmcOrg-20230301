export interface SignInCodeDTO {
    code?: string // 微信 code，required：true
}

export interface SignInPhoneCodeDTO {
    phoneCode?: string // 手机号码 code，required：true
}
