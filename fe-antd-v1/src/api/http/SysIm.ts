import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullIdAndNotEmptyLongSet {
    valueSet?: string[] // 值 set，required：true，format：int64
    id?: string // 主键 id，required：true，format：int64
}

// 加入新用户
export function SysImSessionRefUserJoinUserIdSet(form: NotNullIdAndNotEmptyLongSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/im/session/refUser/join/userIdSet', form, config)
}

export interface SysImSessionContentSendTextDTO {
    createTs?: string // 创建时间的时间戳，required：true，format：int64
    content?: string // 发送的内容，required：true
}

export interface SysImSessionContentSendTextListDTO {
    sessionId?: string // 会话主键 id，format：int64
    contentSet?: SysImSessionContentSendTextDTO[] // 发送内容集合
}

// 发送内容-文字-用户自我
export function SysImSessionContentSendTextUserSelf(form: SysImSessionContentSendTextListDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/im/session/content/send/text/userSelf', form, config)
}

export interface SysImSessionInsertOrUpDateDTO {
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 会话名
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    type?: number // 会话类型：101 私聊 201 群聊 301 客服，备注：只有在新建时，该值才有效，required：true，format：int32
}

// 新增/修改
export function SysImSessionInsertOrUpdate(form: SysImSessionInsertOrUpDateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/im/session/insertOrUpdate', form, config)
}

export interface SysImSessionPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 会话名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    type?: number // 会话类型：101 私聊 201 群聊 301 客服，format：int32
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysImSessionDO {
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: number // 会话类型：101 私聊 201 群聊 301 客服，format：int32
    version?: number // 乐观锁，format：int32
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 会话名
    belongId?: string // 归属者主键 id（群主），备注：如果为客服类型时，群主必须是用户，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysImSessionPage(form: SysImSessionPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysImSessionDO>('/sys/im/session/page', form, config)
}

export interface SysImSessionContentListDTO {
    backwardFlag?: boolean // 是否向后查询，默认：false 根据 id，往前查询 true 根据 id，往后查询
    pageSize?: number // 本次查询的长度，默认：20，format：int32
    id?: string // 主键 id，如果为 null，则根据 backwardFlag，来查询最大 id或者最小 id，注意：不会查询该 id的数据，format：int64
    sessionId?: string // 会话主键 id，format：int64
}

export interface SysImSessionContentDO {
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    sessionId?: string // 会话主键 id，format：int64
    delFlag?: boolean // 是否逻辑删除
    type?: number // 内容类型，format：int32
    version?: number // 乐观锁，format：int32
    content?: string // 会话内容
    showFlag?: boolean // 是否显示在：用户会话列表中
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    createTs?: string // 创建时间的时间戳，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 查询会话内容-用户自我
export function SysImSessionContentScrollPageUserSelf(form: SysImSessionContentListDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysImSessionContentDO>('/sys/im/session/content/scrollPage/userSelf', form, config)
}
