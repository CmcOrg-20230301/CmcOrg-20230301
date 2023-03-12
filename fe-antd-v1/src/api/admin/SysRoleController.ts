import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotEmptyIdSet {

    idSet: number[] // 主键 idSet

}

// 批量删除
export function SysRoleDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/role/deleteByIdSet', form, config)

}

export interface NotNullId {

    id: number // 主键id {"min":1}

}

export interface SysRoleInfoByIdVO {

    userIdSet?: number[] // 用户 idSet
    menuIdSet?: number[] // 菜单 idSet
    name?: string // 角色名（不能重复）
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    id?: number // 主键id
    createId?: number // 创建人id
    createTime?: string // 创建时间
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
    enableFlag?: boolean // 是否启用
    delFlag?: boolean // 是否逻辑删除
    remark?: string // 备注

}

// 通过主键id，查看详情
export function SysRoleInfoById(form: NotNullId, config?: AxiosRequestConfig) {

    return $http.myProPost<SysRoleInfoByIdVO>('/sys/role/infoById', form, config)

}

export interface SysRoleInsertOrUpdateDTO {

    name: string // 角色名，不能重复
    menuIdSet?: number[] // 菜单 idSet
    userIdSet?: number[] // 用户 idSet
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    enableFlag?: boolean // 是否启用
    remark?: string // 备注
    id?: number // 主键id {"min":1}

}

// 新增/修改
export function SysRoleInsertOrUpdate(form: SysRoleInsertOrUpdateDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/role/insertOrUpdate', form, config)

}

export interface SysRolePageDTO {

    name?: string // 角色名（不能重复）
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    enableFlag?: boolean // 是否启用
    remark?: string // 备注
    current?: number // 第几页
    pageSize?: number // 每页显示条数
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）

}

export interface SysRoleDO {

    name?: string // 角色名（不能重复）
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    id?: number // 主键id
    createId?: number // 创建人id
    createTime?: string // 创建时间
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
    enableFlag?: boolean // 是否启用
    delFlag?: boolean // 是否逻辑删除
    remark?: string // 备注

}

// 分页排序查询
export function SysRolePage(form: SysRolePageDTO, config?: AxiosRequestConfig) {

    return $http.myProPagePost<SysRoleDO>('/sys/role/page', form, config)

}
