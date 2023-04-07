export interface SignSignInNameUpdatePasswordDTO {
    originNewPassword?: string // 前端加密之后的原始新密码，required：true
    oldPassword?: string // 前端加密之后的旧密码，required：true
    newPassword?: string // 前端加密之后的新密码，required：true
}

export interface SignSignInNameSignDeleteDTO {
    currentPassword?: string // 前端加密之后的密码，required：true
}

export interface SignSignInNameSignUpDTO {
    password?: string // 前端加密之后的密码，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    originPassword?: string // 前端加密之后的原始密码，required：true
}

export interface SignSignInNameUpdateAccountDTO {
    newSignInName?: string // 新登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
    currentPassword?: string // 前端加密之后的密码，required：true
}

export interface SignSignInNameSignInPasswordDTO {
    password?: string // 前端加密之后的密码，required：true
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0，required：true
}
