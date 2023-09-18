import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysDictDO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 字典类型
    version?: number // 乐观锁，format：int32
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    uuid?: string // 该字典的 uuid，用于：同步租户字典等操作，备注：不允许修改
    systemFlag?: boolean // 系统内置：是 强制同步给租户 否 不同步给租户
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysDictDO[] // 字典的子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 字典/字典项 名
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
    value?: number // 字典项 value（数字 123...）备注：字典为 -1，format：int32
}

// 通过主键id，查看详情
export function SysDictInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysDictDO>('/sys/dict/infoById', form, config)
}

export interface SysDictPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 字典/字典项 名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    type?: string // 字典类型
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    enableFlag?: boolean // 是否启用
    value?: number // 字典项 value（数字 123...）备注：字典为 -1，format：int32
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 查询：树结构
export function SysDictTree(form: SysDictPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysDictDO>('/sys/dict/tree', form, config)
}

// 分页排序查询
export function SysDictPage(form: SysDictPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysDictDO>('/sys/dict/page', form, config)
}

export interface SysDictInsertOrUpdateDTO {
    systemFlag?: boolean // 系统内置：是 强制同步给租户 否 不同步给租户
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 字典/字典项 名，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    type?: string // 字典类型，required：true
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作，required：true
    value?: number // 字典项 value（数字 123...）备注：字典为 -1，format：int32
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysDictInsertOrUpdate(form: SysDictInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/dict/insertOrUpdate', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysDictDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/dict/deleteByIdSet', form, config)
}

export interface SysDictListByDictKeyDTO {
    dictKey?: string // 字典 key，required：true
}

export interface DictVO {
    name?: string // 显示用
    id?: string // 传值用，format：int64
}

// 通过：dictKey获取字典项集合，备注：会进行缓存
export function SysDictListByDictKey(form: SysDictListByDictKeyDTO, config?: AxiosRequestConfig) {
    return $http.myPost<DictVO[]>('/sys/dict/listByDictKey', form, config)
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}

// 通过主键 idSet，加减排序号
export function SysDictAddOrderNo(form: ChangeNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/dict/addOrderNo', form, config)
}
