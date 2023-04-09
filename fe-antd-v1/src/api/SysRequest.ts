import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";

export interface SysRequestSelfLoginRecordPageDTO {
    current?: string // 第几页，format：int64
    ip?: string // ip
    pageSize?: string // 每页显示条数，format：int64
    category?: string // 请求类别
    region?: string // Ip2RegionUtil.getRegion() 获取到的 ip所处区域
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface PageSysRequestDO {
    total?: string // null，format：int64
    current?: string // null，format：int64
    pages?: string // null，format：int64
    size?: string // null，format：int64
    optimizeCountSql?: boolean // null
    maxLimit?: string // null，format：int64
    searchCount?: boolean // null
    optimizeJoinOfCountSql?: boolean // null
    countId?: string // null

}

export interface SysRequestPageDTO {
    beginCostMs?: string // 耗时开始（毫秒），format：int64
    ip?: string // ip
    pageSize?: string // 每页显示条数，format：int64
    type?: string // 请求类型
    uri?: string // 请求的uri
    successFlag?: boolean // 请求是否成功
    current?: string // 第几页，format：int64
    createId?: string // 创建人id，format：int64
    name?: string // 接口名（备用）
    endCostMs?: string // 耗时结束（毫秒），format：int64
    ctEndTime?: string // 结束时间：创建时间，format：date-time
    category?: string // 请求类别
    region?: string // Ip2RegionUtil.getRegion() 获取到的 ip所处区域
    ctBeginTime?: string // 起始时间：创建时间，format：date-time

}

export interface SysRequestAllAvgVO {
    avgMs?: number // 请求的平均耗时（毫秒），format：int32
    count?: string // 请求的总数，format：int64
}
