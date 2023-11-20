import {getAppDispatch, getAppNav} from "@/MyApp";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
import {signOut} from "@/store/userSlice";
import {RandomStr} from "@/util/StrUtil";
import {GetTenantIdFromStorage, SetTenantIdToStorage} from "@/util/CommonUtil";

// 清除数据
export function ClearStorage() {

    localStorage.clear()
    sessionStorage.clear()

}

// 退出登录
export function SignOut(msg ?: string) {

    const tenantId = GetTenantIdFromStorage();

    ClearStorage()

    SetTenantIdToStorage(tenantId);

    getAppDispatch()(signOut()) // store 退出登录

    getAppNav()(PathConstant.NOT_FOUND_PATH)

    if (msg) {

        ToastSuccess(msg)

    }

}

// 随机昵称
export function RandomNickname() {
    return '用户昵称' + RandomStr(6).toUpperCase()
}
