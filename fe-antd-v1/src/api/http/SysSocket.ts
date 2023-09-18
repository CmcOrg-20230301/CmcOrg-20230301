import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量：启用socket
export function SysSocketEnableByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/socket/enableByIdSet', form, config)
}

// 批量：禁用socket
export function SysSocketDisableByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/socket/disableByIdSet', form, config)
}

export interface SysSocketPageDTO {
    current?: string // 第几页，format：int64
    scheme?: string // 协议
    port?: number // 端口，format：int32
    host?: string // 主机
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    type?: string // socket类型
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysSocketDO {
    scheme?: string // 协议：例如：ws://，wss://，http://，https://，等
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // socket类型
    version?: number // 乐观锁，format：int32
    updateId?: string // 修改人id，format：int64
    path?: string // 路径，备注：以 / 开头
    createTime?: string // 创建时间，format：date-time
    port?: number // 端口，format：int32
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    host?: string // 主机
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysSocketPage(form: SysSocketPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysSocketDO>('/sys/socket/page', form, config)
}
