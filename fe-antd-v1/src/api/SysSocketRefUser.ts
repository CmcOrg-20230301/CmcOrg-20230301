import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysSocketRefUserPageDTO {
    socketId?: string // socket主键 id，format：int64
    current?: string // 第几页，format：int64
    scheme?: string // 协议
    port?: number // 端口，format：int32
    nickname?: string // 冗余字段，昵称
    host?: string // 主机
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    type?: string // socket类型
    userId?: string // 用户主键 id，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysSocketRefUserDO {
    scheme?: string // 冗余字段，协议
    ip?: string // ip
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 冗余字段，socket类型
    version?: number // 乐观锁，format：int32
    userId?: string // 用户主键 id，format：int64
    socketId?: string // socket主键 id，format：int64
    updateId?: string // 修改人id，format：int64
    path?: string // 路径
    onlineType?: string // socket 在线状态
    createTime?: string // 创建时间，format：date-time
    port?: number // 冗余字段，端口，format：int32
    createId?: string // 创建人id，format：int64
    nickname?: string // 冗余字段，用户昵称
    host?: string // 冗余字段，主机
    userAgentJsonStr?: string // User-Agent信息对象，json字符串
    id?: string // 主键id，format：int64
    region?: string // Ip2RegionUtil.getRegion() 获取到的 ip所处区域
    category?: string // 请求类别
    enableFlag?: boolean // 是否启用
    jwtHashRemainMs?: string // jwtHash剩余时间，单位：毫秒，format：int64
    jwtHash?: string // jwtHash
}

// 分页排序查询
export function SysSocketRefUserPage(form: SysSocketRefUserPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysSocketRefUserDO>('/sys/socketRefUser/page', form, config)
}
