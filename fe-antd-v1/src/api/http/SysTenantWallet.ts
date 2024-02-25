import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量冻结
export function SysTenantWalletFrozenByIdSet(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/tenantWallet/frozenByIdSet', form, config)
}

export interface ChangeBigDecimalNumberIdSetDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: number // 需要改变的数值，required：true
}

// 通过租户主键 idSet，加减可提现的钱
export function SysTenantWalletAddWithdrawableMoneyBackground(form: ChangeBigDecimalNumberIdSetDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/tenantWallet/addWithdrawableMoney/background', form, config)
}

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

// 通过租户主键id，查看详情
export function SysTenantWalletInfoById(form: NotNullLong, config?: IHttpConfig) {
    return $http.myProPost<SysUserWalletDO>('/sys/tenantWallet/infoById', form, config)
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
export function SysTenantWalletPage(form: SysUserWalletPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysUserWalletDO>('/sys/tenantWallet/page', form, config)
}

// 批量解冻
export function SysTenantWalletThawByIdSet(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/tenantWallet/thawByIdSet', form, config)
}

// 查询：树结构
export function SysTenantWalletTree(form: SysUserWalletPageDTO, config?: IHttpConfig) {
    return $http.myProTreePost<SysUserWalletDO>('/sys/tenantWallet/tree', form, config)
}
