import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil";
import {ApiResultVO} from "@/util/HttpUtil";
import PathConstant from "@/model/constant/PathConstant";
import {ToastSuccess} from "@/util/ToastUtil";
import {GetAppNav} from "@/MyApp";
import {SignEmailSignUp, SignEmailSignUpSendCode} from "@/api/http/SignEmail";
import {ISignUpForm} from "@/page/sign/SignUp/SignUp";

import {SignSignInNameSignUp} from "@/api/http/SignSignInName";
import React, {useEffect} from "react";
import {CloseWebSocket} from "@/util/WebSocket/WebSocketUtil";
import {GetTenantId} from "@/util/CommonUtil";
import {SetTenantManageName} from "@/page/sign/SignIn/SignInUtil.ts";
import {SysSignTypeEnum} from "@/model/enum/SysSignTypeEnum.tsx";
import {SignPhoneSignUp, SignPhoneSignUpSendCode} from "@/api/http/SignPhone.ts";

/**
 * 登录，注册页面，打开时的通用操作
 */
export function UseEffectSign(tenantIdRef: React.MutableRefObject<string>, voidFun?: () => void) {

    useEffect(() => {

        tenantIdRef.current = GetTenantId()

        SetTenantManageName(tenantIdRef.current);

        CloseWebSocket() // 关闭 webSocket

        if (voidFun) {

            voidFun()

        }

    }, [])

}

/**
 * 处理表单
 */
export async function SignUpFormHandler(form: ISignUpForm) {

    const date = new Date()
    const originPassword = RSAEncryptPro(form.password, date)
    const password = PasswordRSAEncrypt(form.password, date)

    if (form.signUpType === SysSignTypeEnum.Email.code) { // 如果是：邮箱

        await SignEmailSignUp({

            email: form.account,
            password,
            originPassword,
            code: form.code,
            tenantId: form.tenantId

        }).then(res => {

            SignUpSuccess(res, form.tenantId)

        })

    } else if (form.signUpType === SysSignTypeEnum.Phone.code) { // 如果是：手机

        await SignPhoneSignUp({

            phone: form.account,
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

    if (form.signUpType === SysSignTypeEnum.Email.code) { // 如果是：邮箱

        await SignEmailSignUpSendCode({email: form.account, tenantId: form.tenantId}).then(res => {

            ToastSuccess(res.msg)

        })

    } else if (form.signUpType === SysSignTypeEnum.Phone.code) { // 如果是：手机

        await SignPhoneSignUpSendCode({phone: form.account, tenantId: form.tenantId}).then(res => {

            ToastSuccess(res.msg)

        })

    }

}
