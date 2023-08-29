import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysTenantInsertOrUpdateDTO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    userIdSet?: string[] // 用户 idSet，format：int64
    name?: string // 租户名，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    parentId?: string // 父节点id（顶级则为0），format：int64
}

// 新增/修改
export function SysTenantInsertOrUpdate(form: SysTenantInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/insertOrUpdate', form, config)
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
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysTenantDO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysTenantDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户id，format：int64
    name?: string // 租户名
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 查询：树结构
export function SysTenantTree(form: SysTenantPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysTenantDO>('/sys/tenant/tree', form, config)
}

// 分页排序查询
export function SysTenantPage(form: SysTenantPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysTenantDO>('/sys/tenant/page', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysTenantInfoByIdVO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    userIdSet?: string[] // 用户 idSet，format：int64
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysTenantDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户id，format：int64
    name?: string // 租户名
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 通过主键id，查看详情
export function SysTenantInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysTenantInfoByIdVO>('/sys/tenant/infoById', form, config)
}

// 通过主键id，获取租户名
export function SysTenantGetNameById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/getNameById', form, config)
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}

// 通过主键 idSet，加减排序号
export function SysTenantAddOrderNo(form: ChangeNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenant/addOrderNo', form, config)
}
