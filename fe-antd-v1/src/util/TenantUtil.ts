import {GetTenantIdFromStorage} from "@/util/CommonUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {GetUserSelfInfo} from "@/MyApp.tsx";

/**
 * 是否是：当前租户
 */
export function CurrentTenantFlag(tenantId ?: string) {

    const storageTenantId = GetTenantIdFromStorage();

    if (storageTenantId === CommonConstant.TOP_TENANT_ID_STR) {

        return false

    }

    return storageTenantId === tenantId

}

/**
 * 当前租户是否是：顶级租户
 */
export function CurrentTenantTopFlag() {

    const userSelfInfoVO = GetUserSelfInfo();

    return userSelfInfoVO.tenantId === CommonConstant.TOP_TENANT_ID_STR

}
