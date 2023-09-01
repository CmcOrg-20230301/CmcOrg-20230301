import {getAppDispatch, getAppNav} from "@/MyApp";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
import {signOut} from "@/store/userSlice";
import {RandomStr} from "@/util/StrUtil";
import {GetTenantIdFromStorage, SetTenantIdToStorage} from "@/util/CommonUtil";

// 退出登录
export function SignOut(msg ?: string) {

    const tenantId = GetTenantIdFromStorage();

    localStorage.clear()
    sessionStorage.clear()

    SetTenantIdToStorage(tenantId);

    getAppDispatch()(signOut()) // store 退出登录

    getAppNav()(`${PathConstant.SIGN_IN_PATH}?tenantId=${tenantId}`)

    if (msg) {

        ToastSuccess(msg)

    }

}

// 随机昵称
export function RandomNickname() {
    return '用户昵称' + RandomStr(6).toUpperCase()
}
