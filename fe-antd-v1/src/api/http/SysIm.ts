import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 私聊：拉黑取消
export function SysImSessionApplyPrivateChatBlockCancel(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/block/cancel', form, config)
}

export interface SysImSessionApplyPrivateChatApplySelfPageDTO {
    current?: string // 第几页，format：int64
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysImSessionApplyPrivateChatApplySelfPageVO {
    avatarUrl?: string // 头像地址
    nickname?: string // 昵称
    userId?: string // 用户主键 id，format：int64
}

// 分页排序查询-他人私聊申请列表-自我
export function SysImSessionApplyPrivateChatApplyPageSelf(form: SysImSessionApplyPrivateChatApplySelfPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysImSessionApplyPrivateChatApplySelfPageVO>('/sys/im/session/apply/privateChat/apply/page/self', form, config)
}

export interface SysImSessionApplyPrivateChatApplyDTO {
    applyReason?: string // null
    id?: string // 主键 id，required：true，format：int64
}

// 私聊：申请添加
export function SysImSessionApplyPrivateChatApply(form: SysImSessionApplyPrivateChatApplyDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/apply', form, config)
}

export interface SysImSessionApplyPrivateChatSelfPageDTO {
    current?: string // 第几页，format：int64
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysImSessionApplyPrivateChatSelfPageVO {
    avatarUrl?: string // 头像地址
    nickname?: string // 昵称
    sessionId?: string // 会话主键 id，format：int64
    userId?: string // 用户主键 id，format：int64
}

// 分页排序查询-好友列表-自我
export function SysImSessionApplyPrivateChatPageSelf(form: SysImSessionApplyPrivateChatSelfPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysImSessionApplyPrivateChatSelfPageVO>('/sys/im/session/apply/privateChat/page/self', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

// 私聊：申请取消
export function SysImSessionApplyPrivateChatApplyCancel(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/apply/cancel', form, config)
}

// 私聊：同意添加
export function SysImSessionApplyPrivateChatAgree(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/agree', form, config)
}

export interface SysImSessionSelfPageDTO {
    current?: string // 第几页，format：int64
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    privateChatRefUserId?: string // 私聊关联的另外一个用户主键 id，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysImSessionDO {
    showName?: string // 该会话的展示名称
    avatarFileId?: string // 头像 fileId（文件主键 id），format：int64
    lastContent?: string // 最后一条消息
    lastContentCreateTs?: string // 最后一条消息的创建时间戳，format：int64
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: number // 会话类型：101 私聊 201 群聊 301 客服，format：int32
    version?: number // 乐观锁，format：int32
    lastReceiveContentTs?: string // 最后一次接受到消息时的时间戳，默认为：-1，备注：该字段用于：排序，format：int64
    privateChatRefUserId?: string // 私聊关联的另外一个用户主键 id，format：int64
    unreadContentTotal?: number // 未读消息的总数量，format：int32
    lastContentType?: number // 最后一条消息的内容类型，format：int32
    showAvatarFileId?: string // 该会话的展示头像 fileId，format：int64
    updateId?: string // 修改人id，format：int64
    lastOpenTs?: string // 我最后一次打开该会话的时间戳，format：int64
    createTime?: string // 创建时间，format：date-time
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 会话名
    belongId?: string // 归属者主键 id（群主），备注：如果为客服类型时，群主必须是用户，format：int64
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询-会话列表-自我
export function SysImSessionPageSelf(form: SysImSessionSelfPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysImSessionDO>('/sys/im/session/page/self', form, config)
}

// 私聊：删除
export function SysImSessionApplyPrivateChatDelete(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/delete', form, config)
}

// 私聊：申请隐藏
export function SysImSessionApplyPrivateChatApplyHidden(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/apply/hidden', form, config)
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
export function SysImSessionContentScrollPageUserSelf(form: SysImSessionContentListDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysImSessionContentDO>('/sys/im/session/content/scrollPage/userSelf', form, config)
}

export interface SysImSessionApplyPrivateChatApplyInitiateSelfPageDTO {
    current?: string // 第几页，format：int64
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysImSessionApplyPrivateChatApplyInitiateSelfPageVO {
    avatarUrl?: string // 头像地址
    nickname?: string // 昵称
    userId?: string // 用户主键 id，format：int64
}

// 分页排序查询-私聊申请他人列表-自我
export function SysImSessionApplyPrivateChatApplyInitiatePageSelf(form: SysImSessionApplyPrivateChatApplyInitiateSelfPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysImSessionApplyPrivateChatApplyInitiateSelfPageVO>('/sys/im/session/apply/privateChat/apply/initiate/page/self', form, config)
}

export interface NotNullIdAndLongSet {
    valueSet?: string[] // 值 set，format：int64
    id?: string // 主键 id，required：true，format：int64
}

export interface LongObjectMapVOSysImSessionRefUserQueryRefUserInfoMapVO {
    map?: object // map对象
}

// 查询：当前会话的用户信息，map
export function SysImSessionRefUserQueryRefUserInfoMap(form: NotNullIdAndLongSet, config?: IHttpConfig) {
    return $http.myPost<LongObjectMapVOSysImSessionRefUserQueryRefUserInfoMapVO>('/sys/im/session/refUser/query/refUserInfoMap', form, config)
}

export interface SysImSessionRefUserJoinUserIdSetDTO {
    valueSet?: string[] // 值 set，required：true，format：int64
    privateChatFlag?: boolean // null
    id?: string // 主键 id，required：true，format：int64
}

// 加入新用户
export function SysImSessionRefUserJoinUserIdSet(form: SysImSessionRefUserJoinUserIdSetDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/refUser/join/userIdSet', form, config)
}

export interface SysImSessionQueryCustomerSessionIdUserSelfDTO {
    name?: string // 会话名
    type?: number // 会话类型：101 私聊 201 群聊 301 客服，format：int32
}

// 查询：用户自我，所属客服会话的主键 id
export function SysImSessionQueryCustomerSessionIdUserSelf(form: SysImSessionQueryCustomerSessionIdUserSelfDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/query/customer/sessionId/userSelf', form, config)
}

// 私聊：拉黑
export function SysImSessionApplyPrivateChatBlock(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/block', form, config)
}

export interface SysImSessionContentSendTextDTO {
    createTs?: string // 创建时间的时间戳，required：true，format：int64
    content?: string // 发送的内容，required：true
}

export interface SysImSessionContentSendTextListDTO {
    sessionId?: string // 会话主键 id，format：int64
    contentSet?: SysImSessionContentSendTextDTO[] // 发送内容集合
}

export interface NotNullIdAndNotEmptyLongSet {
    valueSet?: string[] // 值 set，required：true，format：int64
    id?: string // 主键 id，required：true，format：int64
}

// 用户自我-发送内容-文字
export function SysImSessionContentWebSocketSendTextUserSelf(form: SysImSessionContentSendTextListDTO, config?: IHttpConfig) {
    return $http.myPost<NotNullIdAndNotEmptyLongSet>('/sys/im/session/content/webSocket/send/text/userSelf', form, config)
}

export interface SysImSessionApplyPrivateChatFindNewPageDTO {
    current?: string // 第几页，format：int64
    nickname?: string // 昵称
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysImSessionApplyPrivateChatFindNewPageVO {
    avatarUrl?: string // 头像地址
    nickname?: string // 昵称
    userId?: string // 用户主键 id，format：int64
}

// 分页排序查询-搜索新的朋友列表
export function SysImSessionApplyPrivateChatFindNewPage(form: SysImSessionApplyPrivateChatFindNewPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysImSessionApplyPrivateChatFindNewPageVO>('/sys/im/session/apply/privateChat/findNew/page', form, config)
}

export interface SysImSessionPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 会话名
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    queryContentInfoFlag?: boolean // 是否查询：消息相关信息，默认：false
    type?: number // 会话类型：101 私聊 201 群聊 301 客服，format：int32
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询
export function SysImSessionPage(form: SysImSessionPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysImSessionDO>('/sys/im/session/page', form, config)
}

// 更新-最后一次打开会话的时间戳-用户自我
export function SysImSessionRefUserWebSocketUpdateLastOpenTsUserSelf(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/refUser/webSocket/update/lastOpenTs/userSelf', form, config)
}

export interface SysImSessionApplyPrivateChatRejectDTO {
    rejectReason?: string // 拒绝理由
    id?: string // 主键 id，required：true，format：int64
}

// 私聊：拒绝添加
export function SysImSessionApplyPrivateChatReject(form: SysImSessionApplyPrivateChatRejectDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/im/session/apply/privateChat/reject', form, config)
}
