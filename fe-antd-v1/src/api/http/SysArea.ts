import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysAreaPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 区域名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysAreaDO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysAreaDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 区域名
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 查询：树结构
export function SysAreaTree(form: SysAreaPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysAreaDO>('/sys/area/tree', form, config)
}

export interface SysAreaInsertOrUpdateDTO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    deptIdSet?: string[] // 部门 idSet，format：int64
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 区域名，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    parentId?: string // 父节点id（顶级则为0），format：int64
}

// 新增/修改
export function SysAreaInsertOrUpdate(form: SysAreaInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/area/insertOrUpdate', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysAreaDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/area/deleteByIdSet', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysAreaInfoByIdVO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    updateId?: string // 修改人id，format：int64
    deptIdSet?: string[] // 部门 idSet，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysAreaDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 区域名
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 通过主键id，查看详情
export function SysAreaInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysAreaInfoByIdVO>('/sys/area/infoById', form, config)
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}

// 通过主键 idSet，加减排序号
export function SysAreaAddOrderNo(form: ChangeNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/area/addOrderNo', form, config)
}

// 分页排序查询
export function SysAreaPage(form: SysAreaPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysAreaDO>('/sys/area/page', form, config)
}
