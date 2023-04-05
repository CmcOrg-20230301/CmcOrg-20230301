import {PasswordRSAEncrypt, RSAEncrypt} from "@/util/RsaUtil";
import {ApiResultVO} from "@/util/HttpUtil";
import PathConstant from "@/model/constant/PathConstant";
import {ToastSuccess} from "@/util/ToastUtil";
import {getAppNav} from "@/App";
// import {SignEmailSignUp, SignEmailSignUpSendCode} from "@/api/sign/SignEmailController";
import {ISignUpForm} from "@/page/sign/SignUp/SignUp";

// import {SignSignInNameSignUp} from "@/api/sign/SignSignInNameController";

/**
 * 处理表单
 */
export async function SignUpFormHandler(form: ISignUpForm) {

    const originPassword = RSAEncrypt(form.password)
    const password = PasswordRSAEncrypt(form.password)

    if (form.type === '1') { // 如果是：邮箱

        // await SignEmailSignUp({
        //     email: form.account,
        //     password,
        //     originPassword: originPassword,
        //     code: form.code!
        // }).then(res => {
        //
        //     SignUpSuccess(res)
        //
        // })

    } else {

        // await SignSignInNameSignUp({signInName: form.account, password, originPassword: originPassword}).then(res => {
        //
        //     SignUpSuccess(res)
        //
        // })

    }
}

/**
 * 注册成功之后的处理
 */
function SignUpSuccess(res: ApiResultVO) {

    ToastSuccess(res.msg)
    getAppNav()(PathConstant.SIGN_IN_PATH)

}

/**
 * 发送验证码
 */
export async function SendCode(form: ISignUpForm) {

    if (!form.account) {
        return
    }

    if (form.type === '1') { // 如果是：邮箱

        // await SignEmailSignUpSendCode({email: form.account}).then(res => {
        //
        //     ToastSuccess(res.msg)
        //
        // })

    }

}
