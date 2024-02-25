import {GetAppDispatch, GetAppNav} from "@/MyApp";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
import {signOut} from "@/store/userSlice";
import {RandomStr} from "@/util/StrUtil";
import {GetStorageForeverValue, MyLocalStorage, MySessionStorage, SetStorageForeverValue} from "@/util/StorageUtil.ts";
import {SignInVO} from "@/api/http/SignWx.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {SetTenantIdToStorage} from "@/util/CommonUtil.ts";

// 清除数据
export function ClearStorage() {

    MyLocalStorage.clear()
    MySessionStorage.clear()

}

// 退出登录
export function SignOut(msg ?: string, path?: string) {

    const StorageForeverValue = GetStorageForeverValue();

    ClearStorage()

    SetStorageForeverValue(StorageForeverValue);

    GetAppDispatch()(signOut()) // store 退出登录

    GetAppNav()(path || PathConstant.TOP_PATH)

    if (msg) {

        ToastSuccess(msg)

    }

}

/**
 * 登录成功之后的处理
 */
export function SignInSuccess(signInVO: SignInVO, path: string = PathConstant.ADMIN_PATH, showMsg: boolean = true, redirectFlag: boolean = true) {

    const StorageForeverValue = GetStorageForeverValue();

    ClearStorage()

    SetStorageForeverValue(StorageForeverValue);

    GetAppDispatch()(signOut()) // store 退出登录

    if (showMsg) {
        ToastSuccess('欢迎回来~')
    }

    MyLocalStorage.setItem(LocalStorageKey.JWT, signInVO.jwt!)

    MyLocalStorage.setItem(LocalStorageKey.JWT_EXPIRE_TS, signInVO.jwtExpireTs!)

    SetTenantIdToStorage(signInVO.tenantId);

    if (redirectFlag) {
        GetAppNav()(path)
    }

}

// 随机昵称
export function RandomNickname() {
    return '用户昵称' + RandomStr(6).toUpperCase()
}
