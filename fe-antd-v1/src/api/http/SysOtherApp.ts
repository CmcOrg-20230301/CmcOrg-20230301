import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysOtherAppPageDTO {
    current?: string // 第几页，format：int64
    appId?: string // 第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复
    name?: string // 第三方应用名，备注：同一租户不能重复，不同租户可以重复
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    type?: string // 第三方应用类型
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysOtherAppDO {
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    secret?: string // 第三方应用的 secret
    delFlag?: boolean // 是否逻辑删除
    type?: string // 第三方应用类型
    version?: number // 乐观锁，format：int32
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    appId?: string // 第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复
    tenantId?: string // 租户 id，format：int64
    name?: string // 第三方应用名，备注：同一租户不能重复，不同租户可以重复
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysOtherAppPage(form: SysOtherAppPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysOtherAppDO>('/sys/otherApp/page', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

// 通过主键id，查看详情
export function SysOtherAppInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysOtherAppDO>('/sys/otherApp/infoById', form, config)
}

export interface SysOtherAppInsertOrUpdateDTO {
    appId?: string // 第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 第三方应用名，备注：同一租户不能重复，不同租户可以重复，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    secret?: string // 第三方应用的 secret，required：true
    type?: string // 第三方应用类型，required：true
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysOtherAppInsertOrUpdate(form: SysOtherAppInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/insertOrUpdate', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysOtherAppDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/deleteByIdSet', form, config)
}
