import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysFileStorageConfigurationPageDTO {
    defaultFlag?: boolean // 是否是默认存储，备注：只会有一个默认存储
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    type?: number // 文件存储类型，format：int32
    uploadEndpoint?: string // 上传的端点
    bucketPublicName?: string // 公开类型的桶名
    current?: string // 第几页，format：int64
    accessKey?: string // 钥匙
    name?: string // 文件存储配置名
    tenantIdSet?: string[] // 租户 idSet，format：int64
    enableFlag?: boolean // 是否启用
    bucketPrivateName?: string // 私有类型的桶名
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    publicDownloadEndpoint?: string // 公开下载的端点
}

export interface SysFileStorageConfigurationDO {
    defaultFlag?: boolean // 是否是默认存储，备注：只会有一个默认存储
    secretKey?: string // 秘钥
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: number // 文件存储类型，format：int32
    version?: number // 乐观锁，format：int32
    uploadEndpoint?: string // 上传的端点
    bucketPublicName?: string // 公开类型的桶名
    updateId?: string // 修改人id，format：int64
    createTime?: string // 创建时间，format：date-time
    accessKey?: string // 钥匙
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    name?: string // 文件存储配置名
    id?: string // 主键id，format：int64
    enableFlag?: boolean // 是否启用
    bucketPrivateName?: string // 私有类型的桶名
    publicDownloadEndpoint?: string // 公开下载的端点
}

// 分页排序查询
export function SysFileStorageConfigurationPage(form: SysFileStorageConfigurationPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysFileStorageConfigurationDO>('/sys/fileStorageConfiguration/page', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysFileStorageConfigurationDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/fileStorageConfiguration/deleteByIdSet', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

// 通过主键id，查看详情
export function SysFileStorageConfigurationInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysFileStorageConfigurationDO>('/sys/fileStorageConfiguration/infoById', form, config)
}

export interface SysFileStorageConfigurationInsertOrUpdateDTO {
    defaultFlag?: boolean // 是否是默认存储，备注：只会有一个默认存储
    secretKey?: string // 秘钥，required：true
    remark?: string // 备注
    type?: number // 文件存储类型，required：true，format：int32
    uploadEndpoint?: string // 上传的端点，required：true
    bucketPublicName?: string // 公开类型的桶名，required：true
    accessKey?: string // 钥匙，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
    name?: string // 文件存储配置名，required：true
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    bucketPrivateName?: string // 私有类型的桶名，required：true
    publicDownloadEndpoint?: string // 公开下载的端点，required：true
}

// 新增/修改
export function SysFileStorageConfigurationInsertOrUpdate(form: SysFileStorageConfigurationInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/fileStorageConfiguration/insertOrUpdate', form, config)
}
