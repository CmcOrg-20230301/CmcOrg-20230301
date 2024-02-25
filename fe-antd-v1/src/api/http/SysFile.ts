import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

export interface LongObjectMapVOString {
    map?: object // map对象
}

// 批量获取：公开文件的 url
export function SysFileGetPublicUrl(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<LongObjectMapVOString>('/sys/file/getPublicUrl', form, config)
}

export interface SysFilePageDTO {
    originFileName?: string // 文件原始名（包含文件类型）
    publicFlag?: boolean // 是否公开访问
    pageSize?: string // 每页显示条数，format：int64
    remark?: string // 备注
    sysUserTenantEnum?: string // 用户/租户
    current?: string // 第几页，format：int64
    uploadType?: number // 文件上传类型，format：int32
    storageType?: number // 存放文件的服务器类型，format：int32
    belongId?: string // 归属者用户主键 id（拥有全部权限），format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    refId?: string // 关联的 id，format：int64
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysFileDO {
    bucketName?: string // 桶名，例如：be-bucket
    originFileName?: string // 文件原始名（包含文件类型）
    publicFlag?: boolean // 是否公开访问
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    type?: string // 类型
    updateId?: string // 修改人id，format：int64
    refFileId?: string // 引用的文件主键 id，没有则为 -1，如果有值，则文件地址从引用的文件里面获取，但是权限等信息，从本条数据获取，format：int64
    uploadType?: number // 文件上传类型，format：int32
    belongId?: string // 归属者用户主键 id（拥有全部权限），format：int64
    showFileName?: string // 展示用的文件名，默认为：原始文件名（包含文件类型）
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
    storageConfigurationId?: string // 存储文件配置主键 id，format：int64
    newFileName?: string // 新的文件名（包含文件类型），例如：uuid.xxx
    updateTime?: string // 修改时间，format：date-time
    version?: number // 乐观锁，format：int32
    uri?: string // 文件完整路径（包含文件类型，不包含请求端点），例如：avatar/uuid.xxx
    parentId?: string // 上级文件夹的文件主键 id，默认为 0，format：int64
    createTime?: string // 创建时间，format：date-time
    fileSize?: string // 文件大小，单位：byte，format：int64
    createId?: string // 创建人id，format：int64
    tenantId?: string // 租户 id，format：int64
    storageType?: number // 存放文件的服务器类型，format：int32
    extraJson?: string // 额外信息（json格式）
    refId?: string // 关联的 id，format：int64
    fileExtName?: string // 文件类型（不含点），备注：这个是读取文件流的头部信息获得文件类型
}

// 分页排序查询
export function SysFilePage(form: SysFilePageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysFileDO>('/sys/file/page', form, config)
}

export interface SysFilePageSelfDTO {
    current?: string // 第几页，format：int64
    originFileName?: string // 文件原始名（包含文件类型）
    uploadType?: number // 文件上传类型，format：int32
    publicFlag?: boolean // 是否公开访问
    pageSize?: string // 每页显示条数，format：int64
    storageType?: number // 存放文件的服务器类型，format：int32
    tenantIdSet?: string[] // 租户 idSet，format：int64
    remark?: string // 备注
    refId?: string // 关联的 id，format：int64
    enableFlag?: boolean // 是否启用
    sysUserTenantEnum?: string // 用户/租户
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 分页排序查询-租户
export function SysFilePageTenant(form: SysFilePageSelfDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysFileDO>('/sys/file/page/tenant', form, config)
}

// 分页排序查询-自我
export function SysFilePageSelf(form: SysFilePageSelfDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysFileDO>('/sys/file/page/self', form, config)
}

// 批量删除文件：公有和私有
export function SysFileRemoveByFileIdSet(form: NotEmptyIdSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/file/removeByFileIdSet', form, config)
}

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

// 下载文件：私有
export function SysFilePrivateDownload(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<void>('/sys/file/privateDownload', form, config)
}
