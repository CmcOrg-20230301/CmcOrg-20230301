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
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    userId?: string // 用户主键 id，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysUserWalletLogDO {
    totalMoneyPre?: number // 总金额，前
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 记录类型：101 增加 201 减少
    totalMoneyChange?: number // 总金额，变
    totalMoneySuf?: number // 总金额，后
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
    id?: string // 主键id，format：int64
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
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询-用户
export function SysUserWalletLogPageUserSelf(form: SysUserWalletLogUserSelfPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletLogDO>('/sys/userWalletLog/page/userSelf', form, config)
}
