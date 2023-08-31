import {ISignInForm} from "@/page/sign/SignIn/SignIn";
import {PasswordRSAEncrypt} from "@/util/RsaUtil";
import {ToastSuccess} from "@/util/ToastUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {ApiResultVO} from "@/util/HttpUtil";
import {getAppDispatch, getAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import {validate} from "@/util/ValidatorUtil";
import {SignEmailSignInPassword} from "@/api/http/SignEmail";
import {SignSignInNameSignInPassword} from "@/api/http/SignSignInName";
import {signOut} from "@/store/userSlice";
import SessionStorageKey from "@/model/constant/SessionStorageKey";

/**
 * 处理表单
 */
export async function SignInFormHandler(form: ISignInForm) {

    const password = PasswordRSAEncrypt(form.password) // 密码加密

    if (validate.email.regex.test(form.account)) { // 如果是：邮箱

        await SignEmailSignInPassword({email: form.account, password, tenantId: form.tenantId}).then(res => {
            SignInSuccess(res, form.tenantId)
        })

    } else { // 否则是：登录名

        await SignSignInNameSignInPassword({signInName: form.account, password, tenantId: form.tenantId}).then(res => {
            SignInSuccess(res, form.tenantId)
        });

    }

}

/**
 * 登录成功之后的处理
 */
function SignInSuccess(apiResultVO: ApiResultVO, tenantId: string) {

    localStorage.clear()
    sessionStorage.clear()

    getAppDispatch()(signOut()) // store 退出登录

    ToastSuccess('欢迎回来~')

    localStorage.setItem(LocalStorageKey.JWT, apiResultVO.data)

    localStorage.setItem(LocalStorageKey.TENANT_ID, tenantId)
    sessionStorage.setItem(SessionStorageKey.TENANT_ID, tenantId)

    getAppNav()(PathConstant.ADMIN_PATH)

}
