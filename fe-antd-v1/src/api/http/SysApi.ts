import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysApiTokenDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/api/token/deleteByIdSet', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysApiTokenDO {
    createTime?: string // 创建时间，format：date-time
    tenantId?: string // 租户 id，format：int64
    name?: string // apiToken名
    updateTime?: string // 修改时间，format：date-time
    id?: string // null，format：int64
    userId?: string // 用户 id，format：int64
    token?: string // 调用 api时，传递的 token，格式：uuid
}

// 通过主键id，查看详情
export function SysApiTokenInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysApiTokenDO>('/sys/api/token/infoById', form, config)
}

export interface SysApiTokenPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 名称
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询
export function SysApiTokenPage(form: SysApiTokenPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysApiTokenDO>('/sys/api/token/page', form, config)
}

export interface SysApiTokenInsertOrUpdateDTO {
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 名称，required：true
    id?: string // 主键 id，format：int64
}

// 新增/修改
export function SysApiTokenInsertOrUpdate(form: SysApiTokenInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/api/token/insertOrUpdate', form, config)
}
