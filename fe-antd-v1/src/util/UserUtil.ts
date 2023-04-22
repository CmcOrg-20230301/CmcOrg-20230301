import {getAppNav} from "@/App";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
// import {signOut} from "@/store/userSlice";

// 退出登录
export function SignOut(msg ?: string) {

    localStorage.clear()
    sessionStorage.clear()

    // getAppDispatch()(signOut()) // store 退出登录

    getAppNav()(PathConstant.SIGN_IN_PATH)

    if (msg) {

        ToastSuccess(msg)

    }

}
