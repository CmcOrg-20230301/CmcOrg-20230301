import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysOtherAppPageDTO {
    textReplyContent?: string // 用户发送文字之后，回复的内容
    openId?: string // 第三方应用的 openId/微信号，例如：接收微信公众号消息时的 ToUserName
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    type?: number // 第三方应用类型，format：int32
    current?: string // 第几页，format：int64
    imageReplyContent?: string // 用户发送图片之后，回复的内容
    qrCode?: string // 二维码，备注：不是二维码图片的地址，而是二维码解码之后的值
    subscribeReplyContent?: string // 用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容
    appId?: string // 第三方应用的 appId，备注：同一个类型下，所有租户不能重复，原因：比如接收公众号消息时，就无法找到具体是哪一个租户
    name?: string // 第三方应用名
    tenantIdSet?: string[] // 租户 idSet，format：int64
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysOtherAppDO {
    textReplyContent?: string // 用户发送文字之后，回复的内容
    openId?: string // 第三方应用的 openId/微信号，例如：接收微信公众号消息时的 ToUserName，不能重复
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    secret?: string // 第三方应用的 secret
    delFlag?: boolean // 是否逻辑删除
    type?: number // 第三方应用类型，format：int32
    version?: number // 乐观锁，format：int32
    updateId?: string // 修改人id，format：int64
    imageReplyContent?: string // 用户发送图片之后，回复的内容
    qrCode?: string // 二维码，备注：不是二维码图片的地址，而是二维码解码之后的值
    createTime?: string // 创建时间，format：date-time
    subscribeReplyContent?: string // 用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容
    createId?: string // 创建人id，format：int64
    appId?: string // 第三方应用的 appId，备注：同一个类型下，所有租户不能重复，原因：比如接收公众号消息时，就无法找到具体是哪一个租户
    tenantId?: string // 租户 id，format：int64
    name?: string // 第三方应用名
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysOtherAppPage(form: SysOtherAppPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysOtherAppDO>('/sys/otherApp/page', form, config)
}

export interface SysOtherAppOfficialAccountMenuInsertOrUpdateDTO {
    buttonType?: string // 按钮类型
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    otherAppId?: string // 所属的，第三方应用相关配置主键 id，format：int64
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 菜单名，备注：一级菜单最多4个汉字，二级菜单最多8个汉字，多出来的部分将会以 ... 代替，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    replyContent?: string // 回复的内容，备注：一般是点击按钮之后，回复的内容
    value?: string // 如果是按钮，则表示按钮的 key，如果是链接，则表示是 url，required：true
    enableFlag?: boolean // 是否启用
    parentId?: string // 父节点id（顶级则为0），format：int64
}

// 新增/修改
export function SysOtherAppOfficialAccountMenuInsertOrUpdate(form: SysOtherAppOfficialAccountMenuInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/officialAccount/menu/insertOrUpdate', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

export interface SysOtherAppOfficialAccountMenuDO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: number // 类型，format：int32
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    updateId?: string // 修改人id，format：int64
    buttonType?: string // 按钮类型
    createTime?: string // 创建时间，format：date-time
    children?: SysOtherAppOfficialAccountMenuDO[] // 子节点
    createId?: string // 创建人id，format：int64
    otherAppId?: string // 所属的，第三方应用相关配置主键 id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 菜单名，备注：一级菜单最多4个汉字，二级菜单最多8个汉字，多出来的部分将会以 ... 代替
    id?: string // 主键 id，format：int64
    replyContent?: string // 回复的内容，备注：一般是点击按钮之后，回复的内容
    enableFlag?: boolean // 是否启用
    value?: string // 如果是按钮，则表示按钮的 key，如果是链接，则表示是 url
}

// 通过主键id，查看详情
export function SysOtherAppOfficialAccountMenuInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysOtherAppOfficialAccountMenuDO>('/sys/otherApp/officialAccount/menu/infoById', form, config)
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}

// 通过主键 idSet，加减排序号
export function SysOtherAppOfficialAccountMenuAddOrderNo(form: ChangeNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/officialAccount/menu/addOrderNo', form, config)
}

// 通过主键id，查看详情
export function SysOtherAppInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysOtherAppDO>('/sys/otherApp/infoById', form, config)
}

export interface SysOtherAppInsertOrUpdateDTO {
    textReplyContent?: string // 用户发送文字之后，回复的内容
    openId?: string // 第三方应用的 openId/微信号，例如：接收微信公众号消息时的 ToUserName
    remark?: string // 备注
    secret?: string // 第三方应用的 secret，required：true
    type?: number // 第三方应用类型，required：true，format：int32
    imageReplyContent?: string // 用户发送图片之后，回复的内容
    qrCode?: string // 二维码，备注：不是二维码图片的地址，而是二维码解码之后的值
    subscribeReplyContent?: string // 用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容
    appId?: string // 第三方应用的 appId，备注：同一个类型下，所有租户不能重复，原因：比如接收公众号消息时，就无法找到具体是哪一个租户，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 第三方应用名，required：true
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysOtherAppInsertOrUpdate(form: SysOtherAppInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/insertOrUpdate', form, config)
}

export interface SysOtherAppOfficialAccountMenuPageDTO {
    current?: string // 第几页，format：int64
    buttonType?: string // 按钮类型
    otherAppId?: string // 所属的，第三方应用相关配置主键 id，format：int64
    name?: string // 菜单名，备注：一级菜单最多4个汉字，二级菜单最多8个汉字，多出来的部分将会以 ... 代替
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    replyContent?: string // 回复的内容，备注：一般是点击按钮之后，回复的内容
    type?: number // 类型，format：int32
    value?: string // 如果是按钮，则表示按钮的 key，如果是链接，则表示是 url
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 查询：树结构
export function SysOtherAppOfficialAccountMenuTree(form: SysOtherAppOfficialAccountMenuPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysOtherAppOfficialAccountMenuDO>('/sys/otherApp/officialAccount/menu/tree', form, config)
}

// 分页排序查询
export function SysOtherAppOfficialAccountMenuPage(form: SysOtherAppOfficialAccountMenuPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysOtherAppOfficialAccountMenuDO>('/sys/otherApp/officialAccount/menu/page', form, config)
}

// 通过主键id，获取第三方应用名
export function SysOtherAppGetNameById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/getNameById', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysOtherAppDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/deleteByIdSet', form, config)
}

// 批量删除
export function SysOtherAppOfficialAccountMenuDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/otherApp/officialAccount/menu/deleteByIdSet', form, config)
}
