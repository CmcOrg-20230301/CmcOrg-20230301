import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO {
    withdrawMoney?: number // 提现金额，required：true
}

// 新增/修改-用户
export function SysUserWalletWithdrawLogInsertOrUpdateUserSelf(form: SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/insertOrUpdate/userSelf', form, config)
}

export interface SysUserWalletWithdrawLogInsertOrUpdateTenantDTO {
    tenantId?: string // 租户主键 id，required：true，format：int64
    withdrawMoney?: number // 提现金额，required：true
}

// 新增/修改-租户
export function SysUserWalletWithdrawLogInsertOrUpdateTenant(form: SysUserWalletWithdrawLogInsertOrUpdateTenantDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/insertOrUpdate/tenant', form, config)
}

export interface NotNullIdAndStringValue {
    id?: string // 主键 id，required：true，format：int64
    value?: string // 值，required：true
}

// 拒绝-用户的提现记录
export function SysUserWalletWithdrawLogReject(form: NotNullIdAndStringValue, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/reject', form, config)
}

export interface SysUserWalletWithdrawLogPageDTO {
    bankCardNo?: string // 卡号
    pageSize?: string // 每页显示条数，format：int64
    endWithdrawMoney?: number // 提现金额：结束值
    withdrawStatus?: string // 提现状态
    sysUserTenantEnum?: string // 用户/租户
    userId?: string // 用户主键 id，format：int64
    openBankName?: string // 开户行
    payeeName?: string // 收款人姓名
    current?: string // 第几页，format：int64
    rejectReason?: string // 拒绝理由
    branchBankName?: string // 支行
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 提现编号，format：int64
    ctEndTime?: string // 结束时间：创建时间，format：date-time
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    beginWithdrawMoney?: number // 提现金额：开始值
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysUserWalletWithdrawLogDO {
    bankCardNo?: string // 冗余字段：卡号
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    userId?: string // 用户主键 id，format：int64
    withdrawMoney?: number // 提现金额
    withdrawStatus?: string // 提现状态
    openBankName?: string // 冗余字段：开户行
    updateId?: string // 修改人id，format：int64
    payeeName?: string // 冗余字段：收款人姓名
    rejectReason?: string // 拒绝理由
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    branchBankName?: string // 冗余字段：支行
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysUserWalletWithdrawLogPage(form: SysUserWalletWithdrawLogPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/page', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

// 取消-租户
export function SysUserWalletWithdrawLogCancelTenant(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/cancel/tenant', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 受理-用户的提现记录
export function SysUserWalletWithdrawLogAccept(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/accept', form, config)
}

export interface SysUserWalletWithdrawLogPageUserSelfDTO {
    bankCardNo?: string // 卡号
    pageSize?: string // 每页显示条数，format：int64
    endWithdrawMoney?: number // 提现金额：结束值
    withdrawStatus?: string // 提现状态
    sysUserTenantEnum?: string // 用户/租户
    openBankName?: string // 开户行
    payeeName?: string // 收款人姓名
    current?: string // 第几页，format：int64
    rejectReason?: string // 拒绝理由
    branchBankName?: string // 支行
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 提现编号，format：int64
    ctEndTime?: string // 结束时间：创建时间，format：date-time
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    beginWithdrawMoney?: number // 提现金额：开始值
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询-租户
export function SysUserWalletWithdrawLogPageTenant(form: SysUserWalletWithdrawLogPageUserSelfDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/page/tenant', form, config)
}

// 成功-用户的提现记录
export function SysUserWalletWithdrawLogSuccess(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/success', form, config)
}

export interface DictIntegerVO {
    name?: string // 显示用
    id?: number // 传值用，format：int32
}

// 下拉列表-提现状态
export function SysUserWalletWithdrawLogDictListWithdrawStatus(config?: IHttpConfig) {
    return $http.myProPagePost<DictIntegerVO>('/sys/userWalletWithdrawLog/dictList/withdrawStatus', undefined, config)
}

export interface SysUserWalletWithdrawLogInsertOrUpdateDTO {
    withdrawMoney?: number // 提现金额，required：true
    userId?: string // 用户主键 id，format：int64
}

// 新增/修改
export function SysUserWalletWithdrawLogInsertOrUpdate(form: SysUserWalletWithdrawLogInsertOrUpdateDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/insertOrUpdate', form, config)
}

// 通过主键id，查看详情
export function SysUserWalletWithdrawLogInfoById(form: NotNullId, config?: IHttpConfig) {
    return $http.myProPost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/infoById', form, config)
}

// 取消
export function SysUserWalletWithdrawLogCancel(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/cancel', form, config)
}

// 分页排序查询-用户
export function SysUserWalletWithdrawLogPageUserSelf(form: SysUserWalletWithdrawLogPageUserSelfDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/page/userSelf', form, config)
}

// 取消-用户
export function SysUserWalletWithdrawLogCancelUserSelf(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/cancel/userSelf', form, config)
}
