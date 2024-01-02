import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysSmsConfigurationInsertOrUpdateDTO {
    sendCommon?: string // 发送：通用短信
    sendUpdate?: string // 发送：修改手机
    sendSignIn?: string // 发送：登录短信
    defaultFlag?: boolean // 是否是默认短信发送，备注：只会有一个默认短信发送
    signName?: string // 签名内容
    secretKey?: string // 秘钥
    sendSignUp?: string // 发送：注册短信
    secretId?: string // 钥匙
    sendDelete?: string // 发送：账号注销
    remark?: string // 备注
    type?: number // 短信类型：101 阿里 201 腾讯，required：true，format：int32
    sendUpdatePassword?: string // 发送：修改密码
    sendForgetPassword?: string // 发送：忘记密码
    sdkAppId?: string // 短信应用 id
    sendBind?: string // 发送：绑定手机
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 短信名，required：true
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysSmsConfigurationInsertOrUpdate(form: SysSmsConfigurationInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/smsConfiguration/insertOrUpdate', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysSmsConfigurationDO {
    sendCommon?: string // 发送：通用短信
    secretId?: string // 钥匙
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: number // 短信类型：101 阿里 201 腾讯，format：int32
    updateId?: string // 修改人id，format：int64
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
    sendUpdate?: string // 发送：修改手机
    sendSignIn?: string // 发送：登录短信
    defaultFlag?: boolean // 是否是默认短信发送，备注：只会有一个默认短信发送
    signName?: string // 签名内容
    secretKey?: string // 秘钥
    sendSignUp?: string // 发送：注册短信
    updateTime?: string // 修改时间，format：date-time
    sendDelete?: string // 发送：账号注销
    version?: number // 乐观锁，format：int32
    sendUpdatePassword?: string // 发送：修改密码
    sendForgetPassword?: string // 发送：忘记密码
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    sdkAppId?: string // 短信应用 id
    sendBind?: string // 发送：绑定手机
    tenantId?: string // 租户 id，format：int64
    name?: string // 短信名
}

// 通过主键id，查看详情
export function SysSmsConfigurationInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysSmsConfigurationDO>('/sys/smsConfiguration/infoById', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysSmsConfigurationDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/smsConfiguration/deleteByIdSet', form, config)
}

export interface SysSmsConfigurationPageDTO {
    current?: string // 第几页，format：int64
    defaultFlag?: boolean // 是否是默认短信发送，备注：只会有一个默认短信发送
    name?: string // 短信名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    type?: number // 短信类型：101 阿里 201 腾讯，format：int32
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询
export function SysSmsConfigurationPage(form: SysSmsConfigurationPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysSmsConfigurationDO>('/sys/smsConfiguration/page', form, config)
}
