import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysParamDO {
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    uuid?: string // 该参数的 uuid，用于：同步租户参数等操作，备注：不允许修改，并且系统内置参数的 uuid等于 id
    systemFlag?: boolean // 系统内置：是 强制同步给租户 否 不同步给租户
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 配置名，以 uuid为不变值进行使用，不要用此属性
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
    value?: string // 值
}

// 通过主键id，查看详情
export function SysParamInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysParamDO>('/sys/param/infoById', form, config)
}

export interface SysParamInsertOrUpdateDTO {
    systemFlag?: boolean // 系统内置：是 强制同步给租户 否 不同步给租户
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 配置名，以 uuid为不变值进行使用，不要用此属性，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    value?: string // 值，required：true
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysParamInsertOrUpdate(form: SysParamInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/param/insertOrUpdate', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysParamDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/param/deleteByIdSet', form, config)
}

export interface SysParamPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 配置名，以 uuid为不变值进行使用，不要用此属性
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询
export function SysParamPage(form: SysParamPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysParamDO>('/sys/param/page', form, config)
}
