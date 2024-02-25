import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysUserDO {
    wxOpenId?: string // 微信 openId，可以为空，wxAppId + wxOpenId 全租户唯一
    wxAppId?: string // 微信 appId，可以为空，wxAppId + wxOpenId 全租户唯一，备注：因为微信对不同的公众号或者小程序，会提供相同的 wxAppId，所以需要加上 wxOpenId，进行唯一性检查
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否注销，未使用，而是采取直接删除的方式，目的：防止数据量越来越大
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    updateId?: string // 修改人id，format：int64
    password?: string // 密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】
    createTime?: string // 创建时间，format：date-time
    phone?: string // 手机号，可以为空
    createId?: string // 创建人id，format：int64
    signInName?: string // 登录名，可以为空
    tenantId?: string // 租户 id，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，可以为空
}

export interface SysUserInfoByIdVO {
    wxOpenId?: string // 微信 openId，可以为空，wxAppId + wxOpenId 全租户唯一
    bio?: string // 个人简介
    remark?: string // 备注
    delFlag?: boolean // 是否注销，未使用，而是采取直接删除的方式，目的：防止数据量越来越大
    sysWxWorkKfAutoAssistantFlag?: boolean // 企业微信-微信客服：当会话状态为：0 未处理时，是否自动交给智能助手接待，默认：true
    updateId?: string // 修改人id，format：int64
    password?: string // 密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】
    children?: SysUserDO[] // 子节点
    nickname?: string // 昵称
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，可以为空
    manageSignInFlag?: boolean // 是否允许登录：后台管理系统
    postIdSet?: string[] // 岗位 idSet，format：int64
    avatarFileId?: string // 头像 fileId（文件主键 id），format：int64
    wxAppId?: string // 微信 appId，可以为空，wxAppId + wxOpenId 全租户唯一，备注：因为微信对不同的公众号或者小程序，会提供相同的 wxAppId，所以需要加上 wxOpenId，进行唯一性检查
    updateTime?: string // 修改时间，format：date-time
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    deptIdSet?: string[] // 部门 idSet，format：int64
    createTime?: string // 创建时间，format：date-time
    phone?: string // 手机号，可以为空
    createId?: string // 创建人id，format：int64
    signInName?: string // 登录名，可以为空
    tenantId?: string // 租户 id，format：int64
    roleIdSet?: string[] // 角色 idSet，format：int64
}

// 通过主键id，查看详情
export function SysUserInfoById(form: NotNullId, config?: IHttpConfig) {
    return $http.myProPost<SysUserInfoByIdVO>('/sys/user/infoById', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量：冻结
export function SysUserFreeze(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/user/freeze', form, config)
}

export interface SysUserUpdatePasswordDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    newPassword?: string // 前端加密之后的，新密码
    newOriginPassword?: string // 前端加密之后的原始密码，新密码
}

// 批量：修改密码
export function SysUserUpdatePassword(form: SysUserUpdatePasswordDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/user/updatePassword', form, config)
}

// 是否允许后台登录
export function SysUserManageSignInFlag(config?: IHttpConfig) {
    return $http.myPost<boolean>('/sys/user/manageSignInFlag', undefined, config)
}

// 批量：注销用户
export function SysUserDeleteByIdSet(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/user/deleteByIdSet', form, config)
}

// 刷新：用户jwt私钥后缀
export function SysUserRefreshJwtSecretSuf(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/user/refreshJwtSecretSuf', form, config)
}

// 批量：解冻
export function SysUserThaw(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/user/thaw', form, config)
}

export interface SysUserPageDTO {
    passwordFlag?: boolean // 是否有密码
    wxOpenId?: string // 微信 openId
    signUpType?: string // 请求类别
    ip?: string // ip
    wxAppId?: string // 微信 appId
    pageSize?: string // 每页显示条数，format：int64
    endCreateTime?: string // 创建结束时间，format：date-time
    current?: string // 第几页，format：int64
    beginCreateTime?: string // 创建开始时间，format：date-time
    phone?: string // 手机号码
    signInName?: string // 登录名
    nickname?: string // 昵称
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 主键 id，format：int64
    region?: string // Ip2RegionUtil.getRegion() 获取到的 ip所处区域
    enableFlag?: boolean // 是否正常
    endLastActiveTime?: string // 最近活跃结束时间，format：date-time
    email?: string // 邮箱
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    beginLastActiveTime?: string // 最近活跃开始时间，format：date-time
}

export interface SysUserPageVO {
    passwordFlag?: boolean // 是否有密码
    postIdSet?: string[] // 岗位 idSet，format：int64
    lastActiveTime?: string // 最近活跃时间，format：date-time
    avatarFileId?: string // 头像 fileId（文件主键 id），备注：没有时则为 -1，format：int64
    wxOpenId?: string // 微信 openId
    signUpType?: string // 请求类别
    wxAppId?: string // 微信 appId
    deptIdSet?: string[] // 部门 idSet，format：int64
    phone?: string // 手机号码，会脱敏
    createTime?: string // 创建时间，format：date-time
    signInName?: string // 登录名，会脱敏
    tenantId?: string // 租户 id，format：int64
    nickname?: string // 昵称
    roleIdSet?: string[] // 角色 idSet，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 主键 id，format：int64
    region?: string // Ip2RegionUtil.getRegion() 获取到的 ip所处区域
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，备注：会脱敏
    manageSignInFlag?: boolean // 后台登录
}

// 分页排序查询
export function SysUserPage(form: SysUserPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysUserPageVO>('/sys/user/page', form, config)
}

export interface SysUserDictListDTO {
    addAdminFlag?: boolean // 是否追加 admin账号
    allTenantUserFlag?: boolean // 是否是所有租户下的用户：默认：false
}

export interface DictVO {
    name?: string // 显示用
    id?: string // 传值用，format：int64
}

// 下拉列表
export function SysUserDictList(form: SysUserDictListDTO, config?: IHttpConfig) {
    return $http.myProPagePost<DictVO>('/sys/user/dictList', form, config)
}

// 批量：重置头像
export function SysUserResetAvatar(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/user/resetAvatar', form, config)
}

export interface SysUserInsertOrUpdateDTO {
    postIdSet?: string[] // 岗位 idSet，format：int64
    wxOpenId?: string // 微信 openId
    wxAppId?: string // 微信 appId
    bio?: string // 个人简介
    sysWxWorkKfAutoAssistantFlag?: boolean // 企业微信-微信客服：当会话状态为：0 未处理时，是否自动交给智能助手接待，默认：true
    originPassword?: string // 前端加密之后的原始密码
    password?: string // 前端加密之后的密码
    deptIdSet?: string[] // 部门 idSet，format：int64
    phone?: string // 手机号码，maxLength：100，minLength：0
    signInName?: string // 登录名，maxLength：20，minLength：0
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    nickname?: string // 昵称，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{1,20}$
    roleIdSet?: string[] // 角色 idSet，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 正常/冻结
    email?: string // 邮箱，maxLength：200，minLength：0
    manageSignInFlag?: boolean // 是否允许登录：后台管理系统
}

// 新增/修改
export function SysUserInsertOrUpdate(form: SysUserInsertOrUpdateDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/user/insertOrUpdate', form, config)
}
