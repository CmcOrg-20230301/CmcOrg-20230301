import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil";
import {ApiResultVO} from "@/util/HttpUtil";
import PathConstant from "@/model/constant/PathConstant";
import {ToastSuccess} from "@/util/ToastUtil";
import {getAppNav} from "@/App";
import {SignEmailSignUp, SignEmailSignUpSendCode} from "@/api/SignEmail";
import {ISignUpForm} from "@/page/sign/SignUp/SignUp";

import {SignSignInNameSignUp} from "@/api/SignSignInName";

/**
 * 处理表单
 */
export async function SignUpFormHandler(form: ISignUpForm) {

    const data = new Date()
    const originPassword = RSAEncryptPro(form.password, data)
    const password = PasswordRSAEncrypt(form.password, data)

    if (form.type === '1') { // 如果是：邮箱

        await SignEmailSignUp({

            email: form.account,
            password,
            originPassword,
            code: form.code

        }).then(res => {

            SignUpSuccess(res)

        })

    } else {

        await SignSignInNameSignUp({signInName: form.account, password, originPassword}).then(res => {

            SignUpSuccess(res)

        })

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

        await SignEmailSignUpSendCode({email: form.account}).then(res => {

            ToastSuccess(res.msg)

        })

    }

}
