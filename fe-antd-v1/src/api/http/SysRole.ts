import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysRoleInsertOrUpdateDTO {
    userIdSet?: string[] // 用户 idSet，format：int64
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    tenantId?: string // 租户id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 角色名，不能重复，required：true
    menuIdSet?: string[] // 菜单 idSet，format：int64
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysRoleInsertOrUpdate(form: SysRoleInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/role/insertOrUpdate', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysRoleInfoByIdVO {
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    userIdSet?: string[] // 用户 idSet，format：int64
    menuIdSet?: string[] // 菜单 idSet，format：int64
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户id，format：int64
    name?: string // 角色名（不能重复）
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 通过主键id，查看详情
export function SysRoleInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysRoleInfoByIdVO>('/sys/role/infoById', form, config)
}

export interface SysRolePageDTO {
    current?: string // 第几页，format：int64
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    tenantId?: string // 租户 id，format：int64
    name?: string // 角色名（不能重复）
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysRoleDO {
    updateId?: string // 修改人id，format：int64
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户id，format：int64
    name?: string // 角色名（不能重复）
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    id?: string // 主键id，format：int64
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysRolePage(form: SysRolePageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysRoleDO>('/sys/role/page', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysRoleDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/role/deleteByIdSet', form, config)
}
