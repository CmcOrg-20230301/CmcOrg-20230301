import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysUserWalletLogPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 记录名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    ctEndTime?: string // 结束时间：创建时间，format：date-time
    type?: number // 记录类型：1开头 增加 2开头 减少，format：int32
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    userId?: string // 用户主键 id，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysUserWalletLogDO {
    refData?: string // 关联的数据
    withdrawablePreUseMoneySuf?: number // 可提现的钱，预使用，后
    withdrawablePreUseMoneyPre?: number // 可提现的钱，预使用，前
    withdrawablePreUseMoneyChange?: number // 可提现的钱，预使用，变
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: number // 记录类型：1开头 增加 2开头 减少，format：int32
    version?: number // 乐观锁，format：int32
    userId?: string // 用户主键 id，format：int64
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    withdrawableMoneySuf?: number // 可提现的钱，后
    createId?: string // 创建人id，format：int64
    withdrawableMoneyChange?: number // 可提现的钱，变
    tenantId?: string // 租户 id，format：int64
    name?: string // 记录名
    withdrawableMoneyPre?: number // 可提现的钱，前
    id?: string // 主键 id，format：int64
    refId?: string // 关联的 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysUserWalletLogPage(form: SysUserWalletLogPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletLogDO>('/sys/userWalletLog/page', form, config)
}

export interface SysUserWalletLogUserSelfPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 记录名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    ctEndTime?: string // 结束时间：创建时间，format：date-time
    type?: number // 记录类型：1开头 增加 2开头 减少，format：int32
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询-租户
export function SysUserWalletLogPageTenant(form: SysUserWalletLogUserSelfPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletLogDO>('/sys/userWalletLog/page/tenant', form, config)
}

// 分页排序查询-用户自我
export function SysUserWalletLogPageUserSelf(form: SysUserWalletLogUserSelfPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletLogDO>('/sys/userWalletLog/page/userSelf', form, config)
}
