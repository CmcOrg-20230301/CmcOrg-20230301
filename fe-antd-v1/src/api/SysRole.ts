import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";

export interface SysRoleInsertOrUpdateDTO {
    userIdSet?: string[] // 用户 idSet，format：int64
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    name?: string // 角色名，不能重复，required：true
    menuIdSet?: string[] // 菜单 idSet，format：int64
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysRolePageDTO {
    current?: string // 第几页，format：int64
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    name?: string // 角色名（不能重复）
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}
