import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullLong {
    value?: string // 值，required：true，format：int64
}

export interface SysUserBankCardDO {
    orderNo?: number // 为了：组装树结构，没有其他用途，format：int32
    bankCardNo?: string // 卡号
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    parentId?: string // 上级 id，用于：租户银行卡列表的树形结构展示，没有其他用途，format：int64
    openBankName?: string // 开户行
    updateId?: string // 修改人id，format：int64
    payeeName?: string // 收款人姓名
    createTime?: string // 创建时间，format：date-time
    children?: SysUserBankCardDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    branchBankName?: string // 支行
    id?: string // 用户主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 通过租户主键id，查看详情
export function SysTenantBankCardInfoById(form: NotNullLong, config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserBankCardDO>('/sys/tenantBankCard/infoById', form, config)
}

export interface SysUserBankCardPageDTO {
    payeeName?: string // 收款人姓名
    current?: string // 第几页，format：int64
    bankCardNo?: string // 卡号
    pageSize?: string // 每页显示条数，format：int64
    branchBankName?: string // 支行
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 用户主键 id，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    openBankName?: string // 开户行
}

// 查询：树结构
export function SysTenantBankCardTree(form: SysUserBankCardPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysUserBankCardDO>('/sys/tenantBankCard/tree', form, config)
}

// 分页排序查询
export function SysTenantBankCardPage(form: SysUserBankCardPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserBankCardDO>('/sys/tenantBankCard/page', form, config)
}

export interface SysUserBankCardInsertOrUpdateUserSelfDTO {
    payeeName?: string // 收款人姓名，required：true
    bankCardNo?: string // 卡号，正则表达式：^(\d{16}|\d{19}|\d{17})$，required：true
    tenantId?: string // 租户主键 id，format：int64
    branchBankName?: string // 支行，required：true
    openBankName?: string // 开户行，required：true
}

// 新增/修改-租户
export function SysTenantBankCardInsertOrUpdateTenant(form: SysUserBankCardInsertOrUpdateUserSelfDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/tenantBankCard/insertOrUpdate/tenant', form, config)
}
