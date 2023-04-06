export interface SignSignInNameUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码
    oldPassword?: string // 前端加密之后的旧密码
    newPassword?: string // 前端加密之后的新密码
}

export interface SignSignInNameSignDeleteDTO {
    currentPassword?: string // 前端加密之后的密码
}

export interface SignSignInNameSignUpDTO {
    password?: string // 前端加密之后的密码
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0
    originPassword?: string // 前端加密之后的原始密码
}

export interface SignSignInNameUpdateAccountDTO {
    newSignInName?: string // 新登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0
    currentPassword?: string // 前端加密之后的密码
}

export interface SignSignInNameSignInPasswordDTO {
    password?: string // 前端加密之后的密码
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0
}
