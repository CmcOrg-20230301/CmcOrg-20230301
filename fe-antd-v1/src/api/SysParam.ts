import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysParamInsertOrUpdateDTO {
    name?: string // 配置名，以 id为不变值进行使用，不要用此属性，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    value?: string // 值，required：true
    enableFlag?: boolean // 是否启用
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

export interface SysParamPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 配置名，以 id为不变值进行使用，不要用此属性
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}
