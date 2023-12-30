import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil";
import {ApiResultVO} from "@/util/HttpUtil";
import PathConstant from "@/model/constant/PathConstant";
import {ToastSuccess} from "@/util/ToastUtil";
import {GetAppNav} from "@/MyApp";
import {SignEmailSignUp, SignEmailSignUpSendCode} from "@/api/http/SignEmail";
import {ISignUpForm} from "@/page/sign/SignUp/SignUp";

import {SignSignInNameSignUp} from "@/api/http/SignSignInName";
import {useEffect} from "react";
import {CloseWebSocket} from "@/util/WebSocket/WebSocketUtil";
import {SysTenantGetNameById} from "@/api/http/SysTenant";
import {GetTenantId} from "@/util/CommonUtil";

// 租户名后缀
export const TENANT_NAME_SUF = " - "

export function UseEffectSign(tenantIdRef: React.MutableRefObject<string>, setTenantName: (value: (((prevState: string) => string) | string)) => void) {

    useEffect(() => {

        tenantIdRef.current = GetTenantId()

        SysTenantGetNameById({value: tenantIdRef.current}).then(res => {

            if (res.data) {

                setTenantName(res.data + TENANT_NAME_SUF)

            } else {

                setTenantName("")

            }

        })

        CloseWebSocket() // 关闭 webSocket

    }, [])

}

/**
 * 移除：租户名后缀
 */
export function RemoveTenantNameSuf(tenantName?: string) {

    if (!tenantName) {
        return ""
    }

    const regExp = new RegExp(TENANT_NAME_SUF);

    return tenantName.replace(regExp, "");

}

/**
 * 处理表单
 */
export async function SignUpFormHandler(form: ISignUpForm) {

    const date = new Date()
    const originPassword = RSAEncryptPro(form.password, date)
    const password = PasswordRSAEncrypt(form.password, date)

    if (form.type === '1') { // 如果是：邮箱

        await SignEmailSignUp({

            email: form.account,
            password,
            originPassword,
            code: form.code,
            tenantId: form.tenantId

        }).then(res => {

            SignUpSuccess(res, form.tenantId)

        })

    } else {

        await SignSignInNameSignUp({

            signInName: form.account,
            password,
            originPassword,
            tenantId: form.tenantId

        }).then(res => {

            SignUpSuccess(res, form.tenantId)

        })

    }
}

/**
 * 注册成功之后的处理
 */
function SignUpSuccess(res: ApiResultVO, tenantId: string) {

    ToastSuccess(res.msg)

    GetAppNav()(`${PathConstant.SIGN_IN_PATH}?tenantId=${tenantId}`)

}

/**
 * 发送验证码
 */
export async function SendCode(form: ISignUpForm) {

    if (!form.account) {
        return
    }

    if (form.type === '1') { // 如果是：邮箱

        await SignEmailSignUpSendCode({email: form.account, tenantId: form.tenantId}).then(res => {

            ToastSuccess(res.msg)

        })

    }

}
