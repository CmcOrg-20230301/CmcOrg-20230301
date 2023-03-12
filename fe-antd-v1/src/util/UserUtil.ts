import {getAppNav} from "@/App";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";

// 退出登录
export function SignOut(msg ?: string) {

    localStorage.clear()
    sessionStorage.clear()

    getAppNav()(PathConstant.SIGN_IN_PATH)

    if (msg) {

        ToastSuccess(msg)

    }

}
