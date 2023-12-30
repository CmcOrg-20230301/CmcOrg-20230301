import {ISignInForm} from "@/page/sign/SignIn/SignIn";
import {PasswordRSAEncrypt} from "@/util/RsaUtil";
import {ToastSuccess} from "@/util/ToastUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {ApiResultVO} from "@/util/HttpUtil";
import {GetAppDispatch, GetAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import {Validate} from "@/util/ValidatorUtil";
import {SignEmailSignInPassword} from "@/api/http/SignEmail";
import {SignInVO, SignSignInNameSignInPassword} from "@/api/http/SignSignInName";
import {signOut} from "@/store/userSlice";
import {SetTenantIdToStorage} from "@/util/CommonUtil";
import {ClearStorage} from "@/util/UserUtil";
import {GetServerTimestamp} from "@/util/DateUtil";

/**
 * 处理表单
 */
export async function SignInFormHandler(form: ISignInForm) {

    const password = PasswordRSAEncrypt(form.password) // 密码加密

    if (Validate.email.regex.test(form.account)) { // 如果是：邮箱

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
export function SignInSuccess(apiResultVO: ApiResultVO<SignInVO>, tenantId: string, path: string = PathConstant.ADMIN_PATH, showMsg: boolean = true, redirectFlag: boolean = true) {

    ClearStorage()

    GetAppDispatch()(signOut()) // store 退出登录

    if (showMsg) {
        ToastSuccess('欢迎回来~')
    }

    localStorage.setItem(LocalStorageKey.JWT, apiResultVO.data.jwt!)

    localStorage.setItem(LocalStorageKey.JWT_EXPIRE_TIME, String(GetServerTimestamp() + Number(apiResultVO.data.jwtExpireTime)))

    SetTenantIdToStorage(tenantId);

    if (redirectFlag) {
        GetAppNav()(path)
    }

}
