import {GetTenantIdFromStorage} from "@/util/CommonUtil";
import CommonConstant from "@/model/constant/CommonConstant";

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
