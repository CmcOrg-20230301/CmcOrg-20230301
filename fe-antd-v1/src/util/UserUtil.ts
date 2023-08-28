import {getAppDispatch, getAppNav} from "@/MyApp";
import {ToastSuccess} from "./ToastUtil";
import PathConstant from "@/model/constant/PathConstant";
import {signOut} from "@/store/userSlice";
import {RandomStr} from "@/util/StrUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {UserSelfInfoVO} from "@/api/http/UserSelf";

// 退出登录
export function SignOut(msg ?: string) {

    const userSelfInfo: UserSelfInfoVO = JSON.parse(
        localStorage.getItem(LocalStorageKey.USER_SELF_INFO) || '{}'
    );

    const tenantId = userSelfInfo.tenantId;

    localStorage.clear()
    sessionStorage.clear()

    getAppDispatch()(signOut()) // store 退出登录

    getAppNav()(`${PathConstant.SIGN_IN_PATH}??tenantId=${tenantId ? tenantId : 0}`)

    if (msg) {

        ToastSuccess(msg)

    }

}

// 随机昵称
export function RandomNickname() {
    return '用户昵称' + RandomStr(6).toUpperCase()
}
