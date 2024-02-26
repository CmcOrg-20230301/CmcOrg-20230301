import {DevFlag} from "@/util/SysUtil.ts";
import MyPageDTO from "@/model/dto/MyPageDTO.ts";
import {ToastError} from "@/util/ToastUtil.ts";
import {MyLocalStorage} from "@/util/StorageUtil.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {GetBrowserCategory} from "@/util/BrowserCategoryUtil.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import PathConstant from "@/model/constant/PathConstant.ts";
import axios, {AxiosError, AxiosResponse} from "axios";
import {SignOut} from "@/util/UserUtil.ts";
import {RequestData} from "@ant-design/pro-components";

const TIMEOUT_MSG = '请求超时，请重试'
const BASE_ERROR_MSG = "请求错误："
const REQUEST_ERROR_MSG = "请求失败：服务器未启动"

export interface IHttpConfig {

    url?: string
    baseURL?: string
    fullUrl?: string // 该字段不要赋值，程序会进行赋值
    timeout?: number
    headers?: Record<string, string | number>
    responseType?: 'text' | 'arraybuffer' | 'blob' | 'json'
    method?:
        'OPTIONS'
        | 'GET'
        | 'HEAD'
        | 'POST'
        | 'PUT'
        | 'DELETE'
        | 'TRACE'
        | 'CONNECT'

}

export const BaseConfig: IHttpConfig = {

    baseURL: DevFlag() ? '/api' : window.apiUrl,
    timeout: 30 * 60 * 1000, // 默认 30分钟
    method: "POST",
    responseType: 'json',

}

/**
 * 请求拦截器
 */
export function RequestInterceptors(config: IHttpConfig | undefined, url: string): IHttpConfig {

    if (config) {

        config = {...BaseConfig, ...config}

    } else {

        config = {...BaseConfig}

    }

    if (!config.headers) {
        config.headers = {}
    }

    config.url = url

    if (!config.url.startsWith('http')) {

        // 不以 http开头的，才携带 jwt
        config.headers['Authorization'] =
            MyLocalStorage.getItem(LocalStorageKey.JWT) || ''

        config.headers['category'] = GetBrowserCategory() // 请求类别

        if (config.baseURL) {

            config.fullUrl = config.baseURL + config.url

        } else {

            config.fullUrl = config.url

        }

    } else {

        config.fullUrl = config.url

    }

    return config

}

/**
 * 响应拦截器-成功
 *
 * @return null 则表示需要重试
 */
export function ResponseInterceptorsSuccess<T = string>(result: AxiosResponse, config: IHttpConfig): ApiResultVO<T> | undefined | T | null {

    const hiddenErrorMsgFlag = config.headers?.hiddenErrorMsg // 是否隐藏错误提示

    const res = result.data as ApiResultVO<T>

    if (res.code !== CommonConstant.API_OK_CODE || !res.successFlag) {

        if (res.code === 100111) { // 这个代码需要跳转到：登录页面

            if (!hiddenErrorMsgFlag) {

                if (MyLocalStorage.getItem(LocalStorageKey.JWT)) { // 存在 jwt才提示错误消息

                    ToastError(res.msg)

                }

            }

            SignOut()

            return null

        }

        if (!hiddenErrorMsgFlag) {

            ToastError(res.msg || REQUEST_ERROR_MSG)

            if (!res.msg) {

                RequestErrorAutoReload() // 自动刷新页面

            }

        }

        return undefined // 这里会触发 catch，备注：如果没有 catch，则会报错

    } else {

        return res

    }

}

/**
 * 响应拦截器-错误
 */
export function ResponseInterceptorsError(err: AxiosError, config: IHttpConfig): AxiosError {

    const hiddenErrorMsgFlag = config.headers?.hiddenErrorMsg // 是否隐藏错误提示

    if (hiddenErrorMsgFlag) {
        return err // 这里会触发 catch，备注：如果没有 catch，则会报错
    }

    // 所有的请求错误，例如 500 404 错误，超出 2xx 范围的状态码都会触发该函数。
    let msg: string = err.message

    if (msg === 'Network Error') {

        msg = '连接异常，请重试'

    } else if (msg.includes('timeout')) {

        msg = TIMEOUT_MSG

    } else if (msg.includes('Request failed with status code')) {

        const substring = msg.substring(msg.length - 3);

        msg = '接口【' + substring + '】异常，请联系管理员'

        if (substring === '404' || substring === '500') {

            RequestErrorAutoReload() // 自动刷新页面

        }

    }

    ToastError(msg || (BASE_ERROR_MSG + err.message))

    return err // 这里会触发 catch，备注：如果没有 catch，则会报错

}

export const RequestErrorAutoReloadPathSet = new Set<string>()

RequestErrorAutoReloadPathSet.add(PathConstant.ADMIN_PATH)
RequestErrorAutoReloadPathSet.add(PathConstant.BLANK_LAYOUT_PATH)

// 请求出错的时候，自动刷新页面
function RequestErrorAutoReload() {

    if (RequestErrorAutoReloadPathSet.has(location.pathname)) {

        setTimeout(() => {

            location.reload(); // 自动刷新页面

        }, 2000)

    }

}

/**
 * 针对本系统
 */
export interface ApiResultVO<T = string> {

    code: number
    successFlag: boolean
    msg: string
    data: T
    service: string

}

/**
 * 针对本系统
 */
export interface Page<T> {

    total: number // 总数
    size: number // 每页显示条数，默认 10
    current: number // 当前页
    records: T[] // 查询数据列表

}

interface IHttp {

    myPost<T = string, D = any>(url: string, data?: D, config?: IHttpConfig): Promise<ApiResultVO<T>>

    myProPost<T = string, D = any>(url: string, data?: D, config?: IHttpConfig): Promise<T>

    myTreePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<T[]>

    myProTreePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<RequestData<T>>

    myPagePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<Page<T>>

    myProPagePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<RequestData<T>>

}

export const $http: IHttp = {

    myPost,
    myProPost,

    myTreePost,
    myProTreePost,

    myPagePost,
    myProPagePost,

}

/**
 * post请求
 */
export function myPost<T = string, D = any>(url: string, data?: D, config?: IHttpConfig): Promise<ApiResultVO<T>> {

    return request(url, data, config, false)

}

/**
 * post请求
 */
export function myProPost<T = string, D = any>(url: string, data?: D, config?: IHttpConfig): Promise<T> {

    return request<T, D>(url, data, config, true)

}

/**
 * post请求
 */
export function myTreePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<T[]> {

    return request<T[], D>(url, data, config, true)

}

/**
 * post请求
 */
export function myProTreePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<RequestData<T>> {

    return new Promise<RequestData<T>>((resolve, reject) => {

        request<T[], D>(url, data, config, true).then(res => {

            resolve({

                success: true,
                total: res.length,
                data: res

            })

        }).catch(err => {

            reject(err)

        })

    })

}

/**
 * post请求
 */
export function myPagePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<Page<T>> {

    return request<Page<T>, D>(url, data, config, true)

}

/**
 * post请求
 */
export function myProPagePost<T = string, D extends MyPageDTO = any>(url: string, data?: D, config?: IHttpConfig): Promise<RequestData<T>> {

    return new Promise<RequestData<T>>((resolve, reject) => {

        request<Page<T>, D>(url, data, config, true).then(res => {

            resolve({

                success: true,
                total: res.total,
                data: res.records

            })

        }).catch(err => {

            reject(err)

        })

    })

}

export const MyAxios = axios.create()

/**
 * 执行请求
 */
export function request<T = string, D = any>(url: string, data?: D, configTemp?: IHttpConfig, resDataFlag?: boolean) {

    return new Promise<T>((resolve, reject) => {

        const config = RequestInterceptors(configTemp, url)

        MyAxios.request({

            url: config.fullUrl!,

            method: config.method,

            headers: config.headers,

            timeout: config.timeout,

            data: data as any,

        }).then(result => {

            let res = ResponseInterceptorsSuccess<T>(result, config);

            if (res) {

                if (resDataFlag) {

                    res = res as ApiResultVO<T>;

                    resolve(res.data)

                } else {

                    resolve(res as T)

                }

            } else {

                reject(new Error('请求错误：' + JSON.stringify(config)))

            }

        }).catch(err => {

            reject(ResponseInterceptorsError(err, config))

        })

    })

}