import {ToastWarning} from "./ToastUtil";

export const InDevMsg = "功能开发中，敬请期待~"

export function InDev() {

    ToastWarning(InDevMsg)

}

/**
 * 获取：URLSearchParams
 * 调用：get方法即可获取 url参数里面对应的值
 */
export function GetURLSearchParams() {

    return new URLSearchParams(window.location.search)

}

/**
 * 获取：url参数里面的 租户 id
 */
export function GetTenantId(): string {

    return GetURLSearchParams().get("tenantId") || '0'

}

/**
 * 搜索时转换
 */
export function SearchTransform(valueArr: { label: string, value: string }[], key: string) {

    if (valueArr && valueArr.length) {

        return {[key]: valueArr.map(it => it.value)}

    }

    return valueArr

}
