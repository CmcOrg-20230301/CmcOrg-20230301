import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysUserInfoByIdVO {
    wxOpenId?: string // 微信 openId，可以为空
    avatarFileId?: string // 头像 fileId（文件主键 id），format：int64
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

// 通过主键id，查看详情
export function SysUserInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserInfoByIdVO>('/sys/user/infoById', form, config)
}

export interface SysUserUpdatePasswordDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    newPassword?: string // 前端加密之后的，新密码
    newOriginPassword?: string // 前端加密之后的原始密码，新密码
}

// 批量：修改密码
export function SysUserUpdatePassword(form: SysUserUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/user/updatePassword', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量：注销用户
export function SysUserDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/user/deleteByIdSet', form, config)
}

// 刷新：用户jwt私钥后缀
export function SysUserRefreshJwtSecretSuf(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/user/refreshJwtSecretSuf', form, config)
}

export interface SysUserPageDTO {
    passwordFlag?: boolean // 是否有密码
    wxOpenId?: string // 微信 openId
    pageSize?: string // 每页显示条数，format：int64
    endCreateTime?: string // 创建结束时间，format：date-time
    current?: string // 第几页，format：int64
    beginCreateTime?: string // 创建开始时间，format：date-time
    phone?: string // 手机号码
    signInName?: string // 登录名
    nickname?: string // 昵称
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否正常
    endLastActiveTime?: string // 最近活跃结束时间，format：date-time
    email?: string // 邮箱
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    beginLastActiveTime?: string // 最近活跃开始时间，format：date-time
}

export interface SysUserPageVO {
    passwordFlag?: boolean // 是否有密码
    lastActiveTime?: string // 最近活跃时间，format：date-time
    avatarFileId?: string // 头像 fileId（文件主键 id），备注：没有时则为 -1，format：int64
    wxOpenId?: string // 微信 openId，会脱敏
    updateTime?: string // 修改时间，format：date-time
    phone?: string // 手机号码，会脱敏
    createTime?: string // 创建时间，format：date-time
    signInName?: string // 登录名，会脱敏
    nickname?: string // 昵称
    roleIdSet?: string[] // 角色 idSet，format：int64
    deptIdSet?: string[] // 部门 idSet，format：int64
    postIdSet?: string[] // 岗位 idSet，format：int64
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，备注：会脱敏
}

// 分页排序查询
export function SysUserPage(form: SysUserPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserPageVO>('/sys/user/page', form, config)
}

export interface SysUserDictListDTO {
    addAdminFlag?: boolean // 是否追加 admin账号
}

export interface DictVO {
    name?: string // 显示用
    id?: string // 传值用，format：int64
}

// 下拉列表
export function SysUserDictList(form: SysUserDictListDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<DictVO>('/sys/user/dictList', form, config)
}

// 批量：重置头像
export function SysUserResetAvatar(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/user/resetAvatar', form, config)
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

// 新增/修改
export function SysUserInsertOrUpdate(form: SysUserInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/user/insertOrUpdate', form, config)
}
