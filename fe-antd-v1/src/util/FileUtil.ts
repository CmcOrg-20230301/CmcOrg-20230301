import {RcFile} from "antd/es/upload";
import {NotNullId} from "@/api/http/SysFile";
import {ToastError} from "@/util/ToastUtil";
import {GetBrowserCategory} from "@/util/BrowserCategoryUtil";
import {IInit} from "@/util/UseEffectUtil";
import {GetAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import {SysRequestCategoryEnum} from "@/model/enum/SysRequestCategoryEnum.ts";
import {MyLocalStorage, MySessionStorage} from "@/util/StorageUtil.ts";
import {$http, request} from "@/util/HttpUtil.ts";
import {AxiosResponse} from "axios";

// 获取：文件是否可以预览
export function GetFileCanPreviewFlag(fileName: string) {

    return fileName.endsWith(".txt")

}

// 前往：下载页面
export function GoFileDownloadPage(id: string) {

    const data: IInit = {localStorageData: {}, sessionStorageData: {}}

    const localStorageLength = MyLocalStorage.length();

    for (let i = 0; i < localStorageLength; i++) {

        const key = MyLocalStorage.key(i);

        if (key) {

            const value = MyLocalStorage.getItem(key);

            if (value && value.length < 500) { // 防止大数据

                data.localStorageData![key] = value

            }

        }

    }

    const sessionStorageLength = MySessionStorage.length();

    for (let i = 0; i < sessionStorageLength; i++) {

        const key = MySessionStorage.key(i);

        if (key) {

            const value = MySessionStorage.getItem(key);

            if (value && value.length < 500) { // 防止大数据

                data.sessionStorageData![key] = value

            }

        }

    }

    GetAppNav()(PathConstant.FILE_DOWNLOAD_PATH + "?data=" + encodeURIComponent(JSON.stringify(data)) + "&id=" + id)

}

// 是否需要打开：下载页面进行下载
export function OpenFileDownloadPageFlag(): boolean {

    const browserCategory = GetBrowserCategory();

    // 如果是：微信浏览器，则需要打开下载页面
    if (browserCategory === SysRequestCategoryEnum.ANDROID_BROWSER_WX.code || browserCategory === SysRequestCategoryEnum.IOS_BROWSER_WX.code || browserCategory === SysRequestCategoryEnum.PC_BROWSER_WINDOWS_WX.code || browserCategory === SysRequestCategoryEnum.PC_BROWSER_MAC_WX.code || browserCategory === SysRequestCategoryEnum.PC_BROWSER_LINUX_WX.code) {

        return true;

    }

    return false;

}

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

// 下载文件
// 使用：download(res.data, res.headers['content-disposition'])
export function DownloadByString(
    str: string,
    fileName: string = new Date().getTime() + '.xlsx'
) {

    Download(new Blob([str], {
        type: 'text/plain'
    }), fileName)

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

    request<AxiosResponse>(url, form, {responseType: 'blob'}, false, true).then(res => {

        callBack(res.data as any, res.headers['content-disposition'])

    })

}

// 执行：文件下载
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
export function FileUpload<T = string>(formData: FormData, url: string) {

    return $http.myPost<T>(url, formData, {headers: {'Content-Type': 'multipart/form-data'}})

}

export const AvatarFileTypeList = ["image/jpeg", "image/png", "image/jpg"]

// 检查：头像的文件类型
export function CheckAvatarFileType(fileType: string) {
    return AvatarFileTypeList.includes(fileType)
}

export const ExcelFileTypeList = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]

// 检查：excel文件类型
export function CheckExcelFileType(fileType: string) {
    return ExcelFileTypeList.includes(fileType)
}

export const TxtFileTypeList = ["text/plain"]

// 检查：txt文件类型
export function CheckTxtFileType(fileType: string) {
    return TxtFileTypeList.includes(fileType)
}

export const DocumentFileTypeList = ["text/plain", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "", "text/html"]

// 检查：文档文件类型
export function CheckDocumentFileType(fileType: string) {
    return DocumentFileTypeList.includes(fileType)
}

export const BpmnFileTypeList = ["text/xml", ""]

// 检查：bpmn文件类型
export function CheckBpmnFileType(fileType: string) {
    return BpmnFileTypeList.includes(fileType)
}

// 检查：文件的文件类型，2097152（字节）= 2MB
export function CheckFileSize(fileSize: number, maxSize: number = 2097152) {
    return fileSize <= maxSize
}
