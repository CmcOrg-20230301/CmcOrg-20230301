import $http from "./HttpUtil";
import {RcFile} from "antd/es/upload";
import {NotNullId} from "@/api/SysFile";

// 下载文件：需要这样请求 $http({responseType: 'blob'})
// 使用：download(res.data, res.headers['content-disposition'])
export function download(
    res: any,
    fileName: string = new Date().getTime() + '.xlsx'
) {

    if (!res) {
        throw new Error('download 方法的res参数不能为空')
    }

    const blob = new Blob([res])

    fileName = fileName.includes('filename=')
        ? decodeURIComponent(fileName.split('filename=')[1])
        : fileName

    const link = document.createElement('a')

    link.download = fileName

    link.style.display = 'none'

    link.href = URL.createObjectURL(blob)

    document.body.appendChild(link)

    link.click()

    URL.revokeObjectURL(link.href) // 释放 URL对象

    document.body.removeChild(link)

}

// 文件-管理 文件下载
export function SysFilePrivateDownload(form: NotNullId) {

    $http.request({

        url: '/sys/file/privateDownload',
        responseType: 'blob',
        method: 'post',
        data: form

    }).then(res => {

        download(res.data, res.headers['content-disposition'])

    })

}

// 文件-管理 文件下载
export function SysFileDownload(url: string, form?: any) {

    $http.request({

        url: url,
        responseType: 'blob',
        method: 'post',
        data: form

    }).then(res => {

        download(res.data, res.headers['content-disposition'])

    })

}

// 101 头像
type TSysFileUploadProType = 'AVATAR'

// 文件-管理 文件上传
export function SysFileUpload(file: string | RcFile | Blob, type: TSysFileUploadProType) {

    const formData = new FormData()

    formData.append('file', file)

    formData.append('uploadType', type)

    return $http.myPost<string>('/sys/file/upload', formData, {headers: {'Content-Type': 'multipart/form-data'}})

}

export const AvatarFileTypeList = ["image/jpeg", "image/png", "image/jpg"]

// 检查：头像的文件类型
export function CheckAvatarFileType(avatarFileType: string) {
    return AvatarFileTypeList.includes(avatarFileType)
}

// 检查：文件的文件类型，2097152（字节）= 2MB
export function CheckFileSize(fileSize: number, maxSize: number = 2097152) {
    return fileSize <= maxSize
}
