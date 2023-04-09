import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysDictDO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 字典类型：1 字典 2 字典项
    version?: number // 乐观锁，format：int32
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    name?: string // 字典/字典项 名
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
    value?: number // 字典项 value（数字 123...）备注：字典为 -1，format：int32
}

export interface SysDictPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 字典/字典项 名
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 描述/备注
    type?: string // 类型：1 字典 2 字典项，format：byte
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    enableFlag?: boolean // 启用/禁用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysDictTreeVO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 字典类型：1 字典 2 字典项
    version?: number // 乐观锁，format：int32
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysDictTreeVO[] // 字典的子节点
    createId?: string // 创建人id，format：int64
    name?: string // 字典/字典项 名
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
    value?: number // 字典项 value（数字 123...）备注：字典为 -1，format：int32
}

export interface SysDictInsertOrUpdateDTO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    name?: string // 字典/字典项 名，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    type?: string // 字典类型：1 字典 2 字典项，required：true
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作，required：true
    value?: number // 字典项 value（数字 123...）备注：字典为 -1，format：int32
    enableFlag?: boolean // 是否启用
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}
