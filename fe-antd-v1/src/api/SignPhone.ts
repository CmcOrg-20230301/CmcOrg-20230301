export interface PhoneNotBlankDTO {
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
}

export interface SignPhoneBindAccountDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
}

export interface SignPhoneSignInCodeDTO {
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
}

export interface SignPhoneForgetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
    newPassword?: string // 前端加密之后的新密码
}

export interface SignPhoneUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$
    newPassword?: string // 前端加密之后的新密码
}

export interface SignPhoneSignUpDTO {
    password?: string // 前端加密之后的密码
    code?: string // 手机验证码，正则表达式：^[0-9]{6}$
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
    originPassword?: string // 前端加密之后的原始密码
}

export interface SignPhoneUpdateAccountDTO {
    newPhone?: string // 新手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
    oldPhoneCode?: string // 旧手机验证码，正则表达式：^[0-9]{6}$
    newPhoneCode?: string // 新手机验证码，正则表达式：^[0-9]{6}$
}

export interface NotBlankCodeDTO {
    code?: string // 验证码，正则表达式：^[0-9]{6}$
}

export interface SignPhoneSignInPasswordDTO {
    password?: string // 密码
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
}
