import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";

export default interface MyPageDTO {

    current?: number // 第几页
    pageSize?: number // 每页显示条数
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    order?: MyOrderDTO // 排序字段

}
