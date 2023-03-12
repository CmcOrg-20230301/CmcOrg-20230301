import MyOrderDTO from "@/model/dto/MyOrderDTO";
import {SortOrder} from "antd/es/table/interface";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotEmptyIdSet {

    idSet: number[] // 主键 idSet

}

// 批量：注销用户
export function SysUserDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/user/deleteByIdSet', form, config)

}

export interface SysUserDictListDTO {

    addAdminFlag?: boolean // 是否追加 admin账号

}

export interface DictResultVO {

    id?: number // 传值用
    name?: string // 显示用

}

// 下拉列表
export function SysUserDictList(form: SysUserDictListDTO, config?: AxiosRequestConfig) {

    return $http.myProPagePost<DictResultVO>('/sys/user/dictList', form, config)

}

export interface NotNullId {

    id: number // 主键id {"min":1}

}

export interface SysUserInfoByIdVO {

    roleIdSet?: number[] // 角色 idSet
    nickname?: string // 昵称
    bio?: string // 个人简介
    avatarUri?: string // 头像uri
    enableFlag?: boolean // 是否启用
    delFlag?: boolean // 是否逻辑删除
    jwtSecretSuf?: string // 用户 jwt私钥后缀（uuid）
    password?: string // 密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】
    email?: string // 邮箱，可以为空
    signInName?: string // 登录名，可以为空
    phone?: string // 手机号，可以为空
    tenantId?: number // 租户主键 id
    id?: number // 主键id
    createId?: number // 创建人id
    createTime?: string // 创建时间
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
    remark?: string // 备注

}

// 通过主键id，查看详情
export function SysUserInfoById(form: NotNullId, config?: AxiosRequestConfig) {

    return $http.myProPost<SysUserInfoByIdVO>('/sys/user/infoById', form, config)

}

export interface SysUserInsertOrUpdateDTO {

    signInName?: string // 登录名 {"sizeMax":20,"sizeMin":0,"regexp":"^[\\u4E00-\\u9FA5A-Za-z0-9_-]{2,20}$"}
    email?: string // 邮箱 {"sizeMax":200,"sizeMin":0,"regexp":"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"}
    password?: string // 前端加密之后的密码
    originPassword?: string // 前端加密之后的原始密码
    nickname?: string // 昵称 {"regexp":"^[\\u4E00-\\u9FA5A-Za-z0-9_-]{2,20}$"}
    bio?: string // 个人简介
    avatarUri?: string // 头像uri
    enableFlag?: boolean // 是否正常
    roleIdSet?: number[] // 角色 idSet
    id?: number // 主键id {"min":1}

}

// 新增/修改
export function SysUserInsertOrUpdate(form: SysUserInsertOrUpdateDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/user/insertOrUpdate', form, config)

}

export interface SysUserPageDTO {

    id?: number // 主键 id
    nickname?: string // 昵称
    avatarUri?: string // 头像uri
    signInName?: string // 登录名
    email?: string // 邮箱
    enableFlag?: boolean // 是否正常
    passwordFlag?: boolean // 是否有密码
    current?: number // 第几页
    pageSize?: number // 每页显示条数
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）

}

export interface SysUserPageVO {

    id?: number // 主键id
    nickname?: string // 昵称
    avatarUri?: string // 头像uri
    email?: string // 邮箱，备注：会脱敏
    signInName?: string // 登录名，会脱敏
    enableFlag?: boolean // 是否正常
    passwordFlag?: boolean // 是否有密码
    createTime?: string // 创建时间
    updateTime?: string // 修改时间
    roleIdSet?: number[] // 角色 idSet

}

// 分页排序查询
export function SysUserPage(form: SysUserPageDTO, config?: AxiosRequestConfig) {

    return $http.myProPagePost<SysUserPageVO>('/sys/user/page', form, config)

}

// 批量：刷新用户 jwt私钥后缀
export function SysUserRefreshJwtSecretSuf(form: NotEmptyIdSet, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/user/refreshJwtSecretSuf', form, config)

}

// 批量：重置头像
export function SysUserResetAvatar(form: NotEmptyIdSet, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/user/resetAvatar', form, config)

}

export interface SysUserUpdatePasswordDTO {

    newPassword?: string // 前端加密之后的，新密码
    newOriginPassword?: string // 前端加密之后的原始密码，新密码
    idSet: number[] // 主键 idSet

}

// 批量：修改密码
export function SysUserUpdatePassword(form: SysUserUpdatePasswordDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/user/updatePassword', form, config)

}
