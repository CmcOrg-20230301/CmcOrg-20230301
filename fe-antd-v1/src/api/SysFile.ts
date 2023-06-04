import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

// 上传文件：共有和私有
export function SysFileUpload(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/file/upload', undefined, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

export interface LongObjectMapVOString {
    map?: object // map对象
}

// 批量获取：公开文件的 url
export function SysFileGetPublicUrl(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<LongObjectMapVOString>('/sys/file/getPublicUrl', form, config)
}

// 批量删除文件：共有和私有
export function SysFileRemoveByFileIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/file/removeByFileIdSet', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

// 下载文件：私有
export function SysFilePrivateDownload(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<void>('/sys/file/privateDownload', form, config)
}
