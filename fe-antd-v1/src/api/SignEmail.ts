export interface SignEmailBindAccountDTO {
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
}

export interface SignEmailSignUpDTO {
    password?: string // 前端加密之后的密码
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
    originPassword?: string // 前端加密之后的原始密码
}

export interface EmailNotBlankDTO {
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
}

export interface SignEmailForgetPasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$
    newPassword?: string // 前端加密之后的新密码
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
}

export interface SignEmailUpdateAccountDTO {
    newEmailCode?: string // 新邮箱验证码，正则表达式：^[0-9]{6}$
    newEmail?: string // 新邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
    oldEmailCode?: string // 旧邮箱验证码，正则表达式：^[0-9]{6}$
}

export interface SignEmailUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码
    code?: string // 邮箱验证码，正则表达式：^[0-9]{6}$
    newPassword?: string // 前端加密之后的新密码
}

export interface NotBlankCodeDTO {
    code?: string // 验证码，正则表达式：^[0-9]{6}$
}

export interface SignEmailSignInPasswordDTO {
    password?: string // 密码
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
}
