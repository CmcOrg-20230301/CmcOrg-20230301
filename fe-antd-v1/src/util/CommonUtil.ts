import {ToastWarning} from "./ToastUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import SessionStorageKey from "@/model/constant/SessionStorageKey";

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

    const tenantId = GetURLSearchParams().get("tenantId") || '0';

    SetTenantIdToStorage(tenantId);

    return tenantId

}

/**
 * 设置：TenantId 到 Storage里面
 */
export function SetTenantIdToStorage(tenantId: string) {

    localStorage.setItem(LocalStorageKey.TENANT_ID, tenantId)
    sessionStorage.setItem(SessionStorageKey.TENANT_ID, tenantId)

}

/**
 * 从：Storage里面获取 TenantId
 */
export function GetTenantIdFromStorage() {

    let tenantId = localStorage.getItem(LocalStorageKey.TENANT_ID);

    if (!tenantId) {

        tenantId = sessionStorage.getItem(SessionStorageKey.TENANT_ID);

    }

    return tenantId || '0'

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
