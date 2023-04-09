import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysUserInfoByIdVO {
    avatarUri?: string // 头像uri
    wxOpenId?: string // 微信 openId，可以为空
    bio?: string // 个人简介
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否注销，未使用，而是采取直接删除的方式，目的：防止数据量越来越大
    version?: number // 乐观锁，format：int32
    updateId?: string // 修改人id，format：int64
    password?: string // 密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】
    createTime?: string // 创建时间，format：date-time
    phone?: string // 手机号，可以为空
    createId?: string // 创建人id，format：int64
    signInName?: string // 登录名，可以为空
    roleIdSet?: string[] // 角色 idSet，format：int64
    nickname?: string // 昵称
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，可以为空
}

export interface SysUserUpdatePasswordDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    newPassword?: string // 前端加密之后的，新密码
    newOriginPassword?: string // 前端加密之后的原始密码，新密码
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

export interface SysUserPageDTO {
    avatarUri?: string // 头像uri
    passwordFlag?: boolean // 是否有密码
    current?: string // 第几页，format：int64
    phone?: string // 手机号码
    wxOpenId?: string // 微信 openId
    signInName?: string // 登录名
    nickname?: string // 昵称
    pageSize?: string // 每页显示条数，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否正常
    email?: string // 邮箱
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface PageSysUserPageVO {
    total?: string // null，format：int64
    current?: string // null，format：int64
    pages?: string // null，format：int64
    size?: string // null，format：int64
    optimizeCountSql?: boolean // null
    maxLimit?: string // null，format：int64
    searchCount?: boolean // null
    optimizeJoinOfCountSql?: boolean // null
    countId?: string // null

}

export interface SysUserDictListDTO {
    addAdminFlag?: boolean // 是否追加 admin账号
}

export interface PageDictVO {
    total?: string // null，format：int64
    current?: string // null，format：int64
    pages?: string // null，format：int64
    size?: string // null，format：int64
    optimizeCountSql?: boolean // null
    maxLimit?: string // null，format：int64
    searchCount?: boolean // null
    optimizeJoinOfCountSql?: boolean // null
    countId?: string // null

}

export interface SysUserInsertOrUpdateDTO {
    password?: string // 前端加密之后的密码
    phone?: string // 手机号码，正则表达式：^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$，maxLength：100，minLength：0
    signInName?: string // 登录名，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$，maxLength：20，minLength：0
    nickname?: string // 昵称，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$
    roleIdSet?: string[] // 角色 idSet，format：int64
    bio?: string // 个人简介
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，正则表达式：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$，maxLength：200，minLength：0
    originPassword?: string // 前端加密之后的原始密码
}
