import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullLong {
    value?: string // 值，required：true，format：int64
}

export interface SysUserWalletDO {
    orderNo?: number // 为了：组装树结构，没有其他用途，format：int32
    totalMoney?: number // 总金额
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    withdrawableRealMoney?: number // 实际可提现的钱
    delFlag?: boolean // 是否逻辑删除
    withdrawablePreUseMoney?: number // 可提现的钱：预使用，例如用于：用户充值时，需要扣除租户的可提现的钱时
    version?: number // 乐观锁，format：int32
    parentId?: string // 上级 id，用于：租户钱包列表的树形结构展示，没有其他用途，format：int64
    withdrawableMoney?: number // 可提现的钱
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    children?: SysUserWalletDO[] // 子节点
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    id?: string // 用户主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 通过主键id，查看详情
export function SysUserWalletInfoById(form: NotNullLong, config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserWalletDO>('/sys/userWallet/infoById', form, config)
}

// 通过主键id，查看详情-用户
export function SysUserWalletInfoByIdUserSelf(config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserWalletDO>('/sys/userWallet/infoById/userSelf', undefined, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量冻结
export function SysUserWalletFrozenByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWallet/frozenByIdSet', form, config)
}

export interface SysUserWalletPageDTO {
    current?: string // 第几页，format：int64
    utEndTime?: string // 结束时间：更新时间，format：date-time
    endWithdrawableMoney?: number // 提现金额：结束值
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 用户主键 id，format：int64
    utBeginTime?: string // 起始时间：更新时间，format：date-time
    beginWithdrawableMoney?: number // 提现金额：开始值
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询
export function SysUserWalletPage(form: SysUserWalletPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserWalletDO>('/sys/userWallet/page', form, config)
}

// 批量解冻
export function SysUserWalletThawByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWallet/thawByIdSet', form, config)
}

export interface SysUserWalletRechargeTenantDTO {
    sysPayType?: number // 支付方式，备注：如果为 null，则表示用默认支付方式，format：int32
    tenantId?: string // 租户主键 id，format：int64
    value?: number // 值，required：true
}

export interface BuyVO {
    sysPayType?: number // 实际的支付方式，format：int32
    sysPayConfigurationId?: string // 支付配置主键 id，format：int64
    outTradeNo?: string // 本系统的支付订单号
    payReturnValue?: string // 支付返回的参数
}

// 充值-租户
export function SysUserWalletRechargeTenant(form: SysUserWalletRechargeTenantDTO, config?: AxiosRequestConfig) {
    return $http.myPost<BuyVO>('/sys/userWallet/recharge/tenant', form, config)
}

export interface ChangeBigDecimalNumberIdSetDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: number // 需要改变的数值，required：true
}

// 通过主键 idSet，加减可提现的钱
export function SysUserWalletAddWithdrawableMoneyBackground(form: ChangeBigDecimalNumberIdSetDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/userWallet/addWithdrawableMoney/background', form, config)
}

export interface SysUserWalletRechargeUserSelfDTO {
    sysPayType?: number // 支付方式，备注：如果为 null，则表示用默认支付方式，format：int32
    value?: number // 值，required：true
}

// 充值-用户自我
export function SysUserWalletRechargeUserSelf(form: SysUserWalletRechargeUserSelfDTO, config?: AxiosRequestConfig) {
    return $http.myPost<BuyVO>('/sys/userWallet/recharge/userSelf', form, config)
}
