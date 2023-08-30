import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysSocketRefUserPageDTO {
    scheme?: string // 协议
    ip?: string // ip
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    type?: string // socket类型
    userId?: string // 用户主键 id，format：int64
    socketId?: string // socket主键 id，format：int64
    current?: string // 第几页，format：int64
    onlineType?: string // socket 在线状态
    port?: number // 端口，format：int32
    host?: string // 主机
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 主键 id，format：int64
    region?: string // Ip2RegionUtil.getRegion() 获取到的 ip所处区域
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysSocketRefUserDO {
    scheme?: string // 冗余字段，协议
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 冗余字段，socket类型
    socketId?: string // socket主键 id，format：int64
    updateId?: string // 修改人id，format：int64
    path?: string // 路径
    nickname?: string // 冗余字段，用户昵称
    host?: string // 冗余字段，主机
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
    jwtHash?: string // jwtHash
    jwtHashExpireTs?: string // jwtHash未来过期的时间戳，format：int64
    ip?: string // ip
    updateTime?: string // 修改时间，format：date-time
    version?: number // 乐观锁，format：int32
    userId?: string // 用户主键 id，format：int64
    onlineType?: string // socket 在线状态
    createTime?: string // 创建时间，format：date-time
    port?: number // 冗余字段，端口，format：int32
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户id，format：int64
    userAgentJsonStr?: string // User-Agent信息对象，json字符串
    region?: string // Ip2RegionUtil.getRegion() 获取到的 ip所处区域
    category?: string // 请求类别
}

// 分页排序查询
export function SysSocketRefUserPage(form: SysSocketRefUserPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysSocketRefUserDO>('/sys/socketRefUser/page', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量：下线用户
export function SysSocketRefUserOfflineByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/socketRefUser/offlineByIdSet', form, config)
}
