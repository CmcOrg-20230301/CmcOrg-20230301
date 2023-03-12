import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface AddOrderNoDTO {

    number: number // 统一加减的数值
    idSet: number[] // 主键 idSet

}

// 通过主键 idSet，加减排序号
export function SysDictAddOrderNo(form: AddOrderNoDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/dict/addOrderNo', form, config)

}

export interface NotEmptyIdSet {

    idSet: number[] // 主键 idSet

}

// 批量删除
export function SysDictDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/dict/deleteByIdSet', form, config)

}

export interface NotNullId {

    id: number // 主键id {"min":1}

}

export interface SysDictDO {

    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    name?: string // 字典/字典项 名
    type?: 1 | 2 // 字典类型：1 字典 2 字典项
    value?: number // 字典项 value（数字 123...）备注：字典为 -1
    orderNo?: number // 排序号（值越大越前面，默认为 0）
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
export function SysDictInfoById(form: NotNullId, config?: AxiosRequestConfig) {

    return $http.myProPost<SysDictDO>('/sys/dict/infoById', form, config)

}

export interface SysDictInsertOrUpdateDTO {

    dictKey: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    name: string // 字典/字典项 名
    type: 1 | 2 // 字典类型：1 字典 2 字典项
    value?: number // 字典项 value（数字 123...）备注：字典为 -1
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    enableFlag?: boolean // 是否启用
    remark?: string // 备注
    id?: number // 主键id {"min":1}

}

// 新增/修改
export function SysDictInsertOrUpdate(form: SysDictInsertOrUpdateDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/dict/insertOrUpdate', form, config)

}

export interface SysDictPageDTO {

    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    name?: string // 字典/字典项 名
    type?: number // 类型：1 字典 2 字典项
    remark?: string // 描述/备注
    enableFlag?: boolean // 启用/禁用
    current?: number // 第几页
    pageSize?: number // 每页显示条数
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）

}

// 分页排序查询
export function SysDictPage(form: SysDictPageDTO, config?: AxiosRequestConfig) {

    return $http.myProPagePost<SysDictDO>('/sys/dict/page', form, config)

}

export interface SysDictTreeVO {

    children?: SysDictTreeVO[] // 字典的子节点
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    name?: string // 字典/字典项 名
    type?: 1 | 2 // 字典类型：1 字典 2 字典项
    value?: number // 字典项 value（数字 123...）备注：字典为 -1
    orderNo?: number // 排序号（值越大越前面，默认为 0）
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

// 查询：树结构
export function SysDictTree(form: SysDictPageDTO, config?: AxiosRequestConfig) {

    return $http.myProTreePost<SysDictTreeVO>('/sys/dict/tree', form, config)

}
