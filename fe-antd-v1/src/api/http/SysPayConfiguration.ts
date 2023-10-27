import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysPayConfigurationPageDTO {
    current?: string // 第几页，format：int64
    defaultFlag?: boolean // 是否是默认支付方式，备注：只会有一个默认支付方式
    appId?: string // 支付平台，应用 id
    name?: string // 支付名（不可重复）
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    type?: string // 支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysPayConfigurationDO {
    defaultFlag?: boolean // 是否是默认支付方式，备注：只会有一个默认支付方式
    apiV3Key?: string // 支付平台，商户APIV3密钥
    platformPublicKey?: string // 支付平台，公钥
    merchantSerialNumber?: string // 支付平台，商户证书序列号
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌
    version?: number // 乐观锁，format：int32
    updateId?: string // 修改人id，format：int64
    privateKey?: string // 支付平台，私钥
    createTime?: string // 创建时间，format：date-time
    merchantId?: string // 支付平台，商户号
    createId?: string // 创建人id，format：int64
    serverUrl?: string // 支付平台，网关地址，例如：https://openapi.alipay.com/gateway.do
    appId?: string // 支付平台，应用 id
    tenantId?: string // 租户 id，format：int64
    name?: string // 支付名（不可重复）
    notifyUrl?: string // 支付平台，异步接收地址
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysPayConfigurationPage(form: SysPayConfigurationPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysPayConfigurationDO>('/sys/payConfiguration/page', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysPayConfigurationDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/payConfiguration/deleteByIdSet', form, config)
}

export interface DictVO {
    name?: string // 显示用
    id?: string // 传值用，format：int64
}

// 下拉列表
export function SysPayConfigurationDictList(config?: AxiosRequestConfig) {
    return $http.myProPagePost<DictVO>('/sys/payConfiguration/dictList', undefined, config)
}

export interface SysPayConfigurationInsertOrUpdateDTO {
    defaultFlag?: boolean // 是否是默认支付方式，备注：只会有一个默认支付方式
    apiV3Key?: string // 支付平台，商户APIV3密钥
    platformPublicKey?: string // 支付平台，公钥
    merchantSerialNumber?: string // 支付平台，商户证书序列号
    remark?: string // 备注
    type?: string // 支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌，required：true
    privateKey?: string // 支付平台，私钥，required：true
    merchantId?: string // 支付平台，商户号
    serverUrl?: string // 支付平台，网关地址，例如：https://openapi.alipay.com/gateway.do
    appId?: string // 支付平台，应用 id，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 支付名，required：true
    notifyUrl?: string // 支付平台，异步接收地址
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysPayConfigurationInsertOrUpdate(form: SysPayConfigurationInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/payConfiguration/insertOrUpdate', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

// 通过主键id，查看详情
export function SysPayConfigurationInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysPayConfigurationDO>('/sys/payConfiguration/infoById', form, config)
}
