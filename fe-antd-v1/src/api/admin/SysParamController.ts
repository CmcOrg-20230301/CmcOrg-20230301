import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotEmptyIdSet {

    idSet: number[] // 主键 idSet

}

// 批量删除
export function SysParamDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/param/deleteByIdSet', form, config)

}

export interface NotNullId {

    id: number // 主键id {"min":1}

}

export interface SysParamDO {

    name?: string // 配置名，以 id为不变值进行使用，不要用此属性
    value?: string // 值
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
export function SysParamInfoById(form: NotNullId, config?: AxiosRequestConfig) {

    return $http.myProPost<SysParamDO>('/sys/param/infoById', form, config)

}

export interface SysParamInsertOrUpdateDTO {

    name: string // 配置名，以 id为不变值进行使用，不要用此属性
    value: string // 值
    remark?: string // 备注
    enableFlag?: boolean // 是否启用
    id?: number // 主键id {"min":1}

}

// 新增/修改
export function SysParamInsertOrUpdate(form: SysParamInsertOrUpdateDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/param/insertOrUpdate', form, config)

}

export interface SysParamPageDTO {

    name?: string // 配置名，以 id为不变值进行使用，不要用此属性
    enableFlag?: boolean // 是否启用
    remark?: string // 备注
    current?: number // 第几页
    pageSize?: number // 每页显示条数
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）

}

// 分页排序查询
export function SysParamPage(form: SysParamPageDTO, config?: AxiosRequestConfig) {

    return $http.myProPagePost<SysParamDO>('/sys/param/page', form, config)

}
