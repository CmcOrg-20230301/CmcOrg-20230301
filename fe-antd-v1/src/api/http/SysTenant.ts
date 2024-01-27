import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysTenantInsertOrUpdateDTO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    userIdSet?: string[] // 用户 idSet，format：int64
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 租户名，required：true
    manageName?: string // 管理后台名称
    menuIdSet?: string[] // 菜单 idSet，format：int64
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    parentId?: string // 父节点id（顶级则为0），format：int64
}

// 新增/修改
export function SysTenantInsertOrUpdate(form: SysTenantInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/insertOrUpdate', form, config)
}

export interface DictTreeVO {
    name?: string // 显示用
    id?: string // 传值用，format：int64
    parentId?: string // 父级 id，format：int64
}

// 下拉列表
export function SysTenantDictList(config?: AxiosRequestConfig) {
    return $http.myProPagePost<DictTreeVO>('/sys/tenant/dictList', undefined, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysTenantDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/deleteByIdSet', form, config)
}

export interface SysTenantPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 租户名
    manageName?: string // 管理后台名称
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysTenantDO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    refMenuCount?: string // 关联菜单的数量，format：int64
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    dictCount?: string // 字典数量，format：int64
    updateId?: string // 修改人id，format：int64
    userCount?: string // 用户数量，format：int64
    paramCount?: string // 参数数量，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysTenantDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 租户名
    manageName?: string // 管理后台名称
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysTenantPage(form: SysTenantPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysTenantDO>('/sys/tenant/page', form, config)
}

export interface NotNullLong {
    value?: string // 值，required：true，format：int64
}

// 通过主键id，获取租户名
export function SysTenantGetNameById(form: NotNullLong, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/getNameById', form, config)
}

export interface GetQrCodeVO {
    expireTs?: string // 二维码过期时间戳，format：int64
    qrCodeId?: string // 二维码 id，format：int64
    qrCodeUrl?: string // 二维码的 url地址
}

export interface SysSignConfigurationVO {
    emailSignUpEnable?: boolean // 是否启用：邮箱注册功能，默认启用
    signInNameSignUpEnable?: boolean // 是否启用：用户名注册功能，默认启用
    wxQrCodeSignUp?: GetQrCodeVO // null
    phoneSignUpEnable?: boolean // 是否启用：手机号码注册功能，默认启用
}

// 通过主键id，获取租户相关的配置
export function SysTenantGetConfigurationById(form: NotNullLong, config?: AxiosRequestConfig) {
    return $http.myPost<SysSignConfigurationVO>('/sys/tenant/getConfigurationById', form, config)
}

// 通过主键id，获取租户后台管理系统名
export function SysTenantGetManageNameById(form: NotNullLong, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/getManageNameById', form, config)
}

// 删除租户所有菜单
export function SysTenantDeleteTenantAllMenu(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/deleteTenantAllMenu', form, config)
}

// 批量：冻结
export function SysTenantFreeze(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/freeze', form, config)
}

// 查询：树结构
export function SysTenantTree(form: SysTenantPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysTenantDO>('/sys/tenant/tree', form, config)
}

export interface NotNullIdAndNotEmptyLongSet {
    valueSet?: string[] // 值 set，required：true，format：int64
    id?: string // 主键 id，required：true，format：int64
}

// 执行：同步菜单给租户
export function SysTenantDoSyncMenu(form: NotNullIdAndNotEmptyLongSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/doSyncMenu', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysTenantInfoByIdVO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    refMenuCount?: string // 关联菜单的数量，format：int64
    userIdSet?: string[] // 用户 idSet，format：int64
    menuIdSet?: string[] // 菜单 idSet，format：int64
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    dictCount?: string // 字典数量，format：int64
    updateId?: string // 修改人id，format：int64
    userCount?: string // 用户数量，format：int64
    paramCount?: string // 参数数量，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysTenantDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 租户名
    manageName?: string // 管理后台名称
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 通过主键id，查看详情
export function SysTenantInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysTenantInfoByIdVO>('/sys/tenant/infoById', form, config)
}

// 批量：解冻
export function SysTenantThaw(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/thaw', form, config)
}

// 执行：同步字典给租户
export function SysTenantDoSyncDict(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/doSyncDict', undefined, config)
}

export interface SysMenuDO {
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    icon?: string // 图标
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    uuid?: string // 该菜单的 uuid，用于：同步租户菜单等操作，备注：不允许修改
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    updateId?: string // 修改人id，format：int64
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    router?: string // 路由
    children?: SysMenuDO[] // 子节点
    id?: string // 主键 id，format：int64
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    enableFlag?: boolean // 是否启用
    redirect?: string // 重定向，优先级最高
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    hiddenPageContainerFlag?: boolean // 是否隐藏：PageContainer
    updateTime?: string // 修改时间，format：date-time
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 菜单名
}

// 获取：需要同步给租户的菜单
export function SysTenantGetSyncMenuInfo(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<SysMenuDO[]>('/sys/tenant/getSyncMenuInfo', form, config)
}

// 执行：同步参数给租户
export function SysTenantDoSyncParam(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/doSyncParam', undefined, config)
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}

// 通过主键 idSet，加减排序号
export function SysTenantAddOrderNo(form: ChangeNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/addOrderNo', form, config)
}
