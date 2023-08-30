import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysDeptInsertOrUpdateDTO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    userIdSet?: string[] // 用户 idSet，format：int64
    tenantId?: string // 租户id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 部门名，required：true
    areaIdSet?: string[] // 区域 idSet，format：int64
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    parentId?: string // 父节点id（顶级则为0），format：int64
}

// 新增/修改
export function SysDeptInsertOrUpdate(form: SysDeptInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/dept/insertOrUpdate', form, config)
}

export interface SysDeptPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 部门名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysDeptDO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysDeptDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户id，format：int64
    name?: string // 部门名
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysDeptPage(form: SysDeptPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysDeptDO>('/sys/dept/page', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

// 通过主键id，查看详情
export function SysDeptInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysDeptDO>('/sys/dept/infoById', form, config)
}

// 查询：树结构
export function SysDeptTree(form: SysDeptPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysDeptDO>('/sys/dept/tree', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysDeptDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/dept/deleteByIdSet', form, config)
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}

// 通过主键 idSet，加减排序号
export function SysDeptAddOrderNo(form: ChangeNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/dept/addOrderNo', form, config)
}
