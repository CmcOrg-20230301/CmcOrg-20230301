import $http from "./HttpUtil";
import {RcFile} from "antd/es/upload";
import {NotNullId} from "@/api/http/SysFile";
import {ToastError} from "@/util/ToastUtil";

/**
 * 获取：文件大小字符串
 */
export function GetFileSizeStr(size: number | string): string {

    if (!size && size !== 0 && size !== '0') {
        return ''
    }

    const numberSize = Number(size);

    const toFixed = (numberSize / 1024 / 1024).toFixed(2);

    if (Number(toFixed) === 0) {

        return numberSize + 'Byte'

    }

    return toFixed + 'Mb'

}

/**
 * 检查：blob的类型
 * @return true 检查通过 false 检查不通过
 */
export function CheckBlobType(blob: Blob) {

    if (blob.type === 'application/json') {

        // 将Blob 对象转换成字符串
        const fileReader = new FileReader();

        fileReader.readAsText(blob, 'utf-8');

        fileReader.onload = (e) => {

            const parse = JSON.parse(fileReader.result as any);

            ToastError(parse.msg || '文件下载错误')

        }

        return false

    }

    return true

}

// 下载文件：需要这样请求 $http({responseType: 'blob'})
// 使用：download(res.data, res.headers['content-disposition'])
export function Download(
    blob: Blob,
    fileName: string = new Date().getTime() + '.xlsx'
) {

    if (!blob) {
        throw new Error('Download 方法的res参数不能为空')
    }

    if (!CheckBlobType(blob)) {
        return
    }

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

// 文件下载
export function FileDownload<T>(url: string, callBack: (blob: Blob, fileName?: string) => void, form?: T) {

    $http.request({

        url: url,
        responseType: 'blob',
        method: 'post',
        data: form

    }).then(res => {

        callBack(res.data as any, res.headers['content-disposition'])

    })

}

// 文件下载
export function ExecFileDownload<T>(url: string, form?: T) {

    FileDownload(url, Download, form)

}

export const SysFilePrivateDownloadUrl = '/sys/file/privateDownload'

// 文件-管理 文件下载
export function SysFilePrivateDownload(form: NotNullId) {

    ExecFileDownload(SysFilePrivateDownloadUrl, form)

}

// 101 头像
type TSysFileUploadProType = 'AVATAR'

// 文件-管理 文件上传
export function SysFileUpload(file: string | RcFile | Blob, type: TSysFileUploadProType) {

    const formData = new FormData()

    formData.append('file', file)

    formData.append('uploadType', type)

    return FileUpload(formData, '/sys/file/upload');

}

// 文件上传
export function FileUpload(formData: FormData, url: string) {

    return $http.myPost<string>(url, formData, {headers: {'Content-Type': 'multipart/form-data'}})

}

export const AvatarFileTypeList = ["image/jpeg", "image/png", "image/jpg"]

// 检查：头像的文件类型
export function CheckAvatarFileType(avatarFileType: string) {
    return AvatarFileTypeList.includes(avatarFileType)
}

export const ExcelFileTypeList = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]

// 检查：excel文件类型
export function CheckExcelFileType(excelFileType: string) {
    return ExcelFileTypeList.includes(excelFileType)
}

export const TxtFileTypeList = ["text/plain"]

// 检查：txt文件类型
export function CheckTxtFileType(excelFileType: string) {
    return TxtFileTypeList.includes(excelFileType)
}

// 检查：文件的文件类型，2097152（字节）= 2MB
export function CheckFileSize(fileSize: number, maxSize: number = 2097152) {
    return fileSize <= maxSize
}
