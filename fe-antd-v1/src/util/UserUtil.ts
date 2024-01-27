import {GetAppDispatch, GetAppNav} from "@/MyApp";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
import {signOut} from "@/store/userSlice";
import {RandomStr} from "@/util/StrUtil";
import {GetStorageForeverValue, SetStorageForeverValue} from "@/util/StorageUtil.ts";

// 清除数据
export function ClearStorage() {

    localStorage.clear()
    sessionStorage.clear()

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

// 随机昵称
export function RandomNickname() {
    return '用户昵称' + RandomStr(6).toUpperCase()
}
