export interface NotNullId {
    id?: string // 主键id，format：int64
}

export interface SysUserUpdatePasswordDTO {
    idSet?: array // 主键 idSet
    newPassword?: string // 前端加密之后的，新密码
    newOriginPassword?: string // 前端加密之后的原始密码，新密码
}

export interface NotEmptyIdSet {
    idSet?: array // 主键 idSet
}

export interface SysUserDictListDTO {
    addAdminFlag?: boolean // 是否追加 admin账号
}

export interface SysUserInsertOrUpdateDTO {
    password?: string // 前端加密之后的密码
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0
    nickname?: string // 昵称，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$
    roleIdSet?: array // 角色 idSet
    bio?: string // 个人简介
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
    originPassword?: string // 前端加密之后的原始密码
}
