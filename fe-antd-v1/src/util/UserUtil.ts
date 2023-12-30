import {GetAppDispatch, GetAppNav} from "@/MyApp";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
import {signOut} from "@/store/userSlice";
import {RandomStr} from "@/util/StrUtil";
import {GetTenantIdFromStorage, SetTenantIdToStorage} from "@/util/CommonUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";

// 清除数据
export function ClearStorage() {

    localStorage.clear()
    sessionStorage.clear()

}

// 退出登录
export function SignOut(msg ?: string) {

    const tenantId = GetTenantIdFromStorage();

    const mainUri = localStorage.getItem(LocalStorageKey.MAIN_URI);

    const noJwtUri = localStorage.getItem(LocalStorageKey.NO_JWT_URI);

    ClearStorage()

    SetTenantIdToStorage(tenantId);

    if (mainUri) {

        localStorage.setItem(LocalStorageKey.MAIN_URI, mainUri)

    }

    if (noJwtUri) {

        localStorage.setItem(LocalStorageKey.NO_JWT_URI, noJwtUri)

    }

    GetAppDispatch()(signOut()) // store 退出登录

    GetAppNav()(PathConstant.NOT_FOUND_PATH)

    if (msg) {

        ToastSuccess(msg)

    }

}

// 随机昵称
export function RandomNickname() {
    return '用户昵称' + RandomStr(6).toUpperCase()
}
