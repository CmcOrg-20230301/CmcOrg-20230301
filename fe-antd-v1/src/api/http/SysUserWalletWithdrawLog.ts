import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO {
    payeeName?: string // 收款人姓名，required：true
    bankCardNo?: string // 卡号，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    branchBankName?: string // 支行，required：true
    id?: string // 主键 id，format：int64
    withdrawMoney?: number // 提现金额，required：true
    openBankName?: string // 开户行，required：true
}

// 新增/修改-用户
export function SysUserWalletWithdrawLogInsertOrUpdateUserSelf(form: SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/insertOrUpdate/userSelf', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

// 撤回-用户
export function SysUserWalletWithdrawLogRevokeUserSelf(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/revoke/userSelf', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysUserWalletWithdrawLogDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/deleteByIdSet', form, config)
}

// 提交-用户
export function SysUserWalletWithdrawLogCommitUserSelf(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/commit/userSelf', form, config)
}

export interface NotNullIdAndStringValue {
    id?: string // 主键id，required：true，format：int64
    value?: string // 值，required：true
}

// 拒绝-用户的提现记录
export function SysUserWalletWithdrawLogReject(form: NotNullIdAndStringValue, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/reject', form, config)
}

export interface SysUserWalletWithdrawLogPageDTO {
    payeeName?: string // 收款人姓名
    current?: string // 第几页，format：int64
    bankCardNo?: string // 卡号
    pageSize?: string // 每页显示条数，format：int64
    branchBankName?: string // 支行
    tenantIdSet?: string[] // 租户 idSet，format：int64
    ctEndTime?: string // 结束时间：创建时间，format：date-time
    withdrawStatus?: string // 提现状态
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    userId?: string // 用户主键 id，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    openBankName?: string // 开户行
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
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysUserWalletWithdrawLogPage(form: SysUserWalletWithdrawLogPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/page', form, config)
}

// 受理-用户的提现记录
export function SysUserWalletWithdrawLogAccept(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/accept', form, config)
}

// 成功-用户的提现记录
export function SysUserWalletWithdrawLogSuccess(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/success', form, config)
}

export interface DictIntegerVO {
    name?: string // 显示用
    id?: number // 传值用，format：int32
}

// 下拉列表-提现状态
export function SysUserWalletWithdrawLogDictListWithdrawStatus(config?: AxiosRequestConfig) {
    return $http.myProPagePost<DictIntegerVO>('/sys/userWalletWithdrawLog/dictList/withdrawStatus', undefined, config)
}

// 通过主键id，查看详情
export function SysUserWalletWithdrawLogInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/infoById', form, config)
}

// 通过主键id，查看详情-用户
export function SysUserWalletWithdrawLogInfoByIdUserSelf(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/infoById/userSelf', form, config)
}

export interface SysUserWalletWithdrawLogPageUserSelfDTO {
    payeeName?: string // 收款人姓名
    current?: string // 第几页，format：int64
    bankCardNo?: string // 卡号
    pageSize?: string // 每页显示条数，format：int64
    branchBankName?: string // 支行
    tenantIdSet?: string[] // 租户 idSet，format：int64
    ctEndTime?: string // 结束时间：创建时间，format：date-time
    withdrawStatus?: string // 提现状态
    ctBeginTime?: string // 起始时间：创建时间，format：date-time
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    openBankName?: string // 开户行
}

// 分页排序查询-用户
export function SysUserWalletWithdrawLogPageUserSelf(form: SysUserWalletWithdrawLogPageUserSelfDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletWithdrawLogDO>('/sys/userWalletWithdrawLog/page/userSelf', form, config)
}

// 批量删除-用户
export function SysUserWalletWithdrawLogDeleteByIdSetUserSelf(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWalletWithdrawLog/deleteByIdSet/userSelf', form, config)
}