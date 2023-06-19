import {getAppDispatch, getAppNav} from "@/MyApp";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
import {signOut} from "@/store/userSlice";
import {RandomStr} from "@/util/StrUtil";

// 退出登录
export function SignOut(msg ?: string) {

    localStorage.clear()
    sessionStorage.clear()

    getAppDispatch()(signOut()) // store 退出登录

    getAppNav()(PathConstant.SIGN_IN_PATH)

    if (msg) {

        ToastSuccess(msg)

    }

}

// 随机昵称
export function RandomNickname() {
    return '用户昵称' + RandomStr(6).toUpperCase()
}
