import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysUserWalletDO {
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    totalMoney?: number // 总金额
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    id?: string // 用户主键 id，format：int64
    delFlag?: boolean // 是否逻辑删除
    version?: number // 乐观锁，format：int32
    enableFlag?: boolean // 是否启用
    withdrawableMoney?: number // 可提现的钱
}

// 通过主键id，查看详情
export function SysUserWalletInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserWalletDO>('/sys/userWallet/infoById', form, config)
}

// 通过主键id，查看详情-用户
export function SysUserWalletInfoByIdUserSelf(config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserWalletDO>('/sys/userWallet/infoById/userSelf', undefined, config)
}

export interface SysUserWalletPageDTO {
    current?: string // 第几页，format：int64
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 用户主键 id，format：int64
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询
export function SysUserWalletPage(form: SysUserWalletPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletDO>('/sys/userWallet/page', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysUserWalletDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWallet/deleteByIdSet', form, config)
}

export interface ChangeBigDecimalNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: number // 需要改变的数值，required：true
}

// 通过主键 idSet，加减可提现的钱
export function SysUserWalletAddWithdrawableMoneyBackground(form: ChangeBigDecimalNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWallet/addWithdrawableMoney/background', form, config)
}

export interface SysUserWalletInsertOrUpdateDTO {
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysUserWalletInsertOrUpdate(form: SysUserWalletInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWallet/insertOrUpdate', form, config)
}
