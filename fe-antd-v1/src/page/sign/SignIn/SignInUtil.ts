import {PasswordRSAEncrypt} from "@/util/RsaUtil";
import {GetAppDispatch} from "@/MyApp";
import {Validate} from "@/util/ValidatorUtil";
import {SignEmailSignInPassword} from "@/api/http/SignEmail";
import {SignSignInNameSignInPassword} from "@/api/http/SignSignInName";
import {SignInSuccess} from "@/util/UserUtil";
import {SysTenantGetManageNameById} from "@/api/http/SysTenant.ts";
import {setTenantManageName} from "@/store/commonSlice.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {SysSignTypeEnum} from "@/model/enum/SysSignTypeEnum.tsx";
import {SignPhoneSignInCode, SignPhoneSignInPassword} from "@/api/http/SignPhone.ts";
import {ISignInForm} from "./SignIn";
import {SignSingleSignInCodePhone} from "@/api/http/SignSingle.ts";

/**
 * 设置：后台系统名
 */
export function SetTenantManageName(tenantId?: string) {

    if (!tenantId) {
        tenantId = CommonConstant.TOP_TENANT_ID_STR
    }

    SysTenantGetManageNameById({value: tenantId}).then(res => {

        GetAppDispatch()(setTenantManageName(res.data))

    })

}

/**
 * 处理表单
 */
export async function SignInFormHandler(form: ISignInForm) {

    if (form.signInType === SysSignTypeEnum.SignInName.code) {

        const password = PasswordRSAEncrypt(form.password) // 密码加密

        if (Validate.email.regex.test(form.account)) { // 如果是：邮箱

            await SignEmailSignInPassword({
                email: form.account,
                password,
                tenantId: form.tenantId
            }).then(res => {

                SignInSuccess(res.data)

            })

        } else { // 否则是：登录名

            await SignSignInNameSignInPassword({

                signInName: form.account,

                password,

                tenantId: form.tenantId

            }).then(res => {

                SignInSuccess(res.data)

            });

        }

    } else if (form.signInType === SysSignTypeEnum.Phone.code) {

        if (form.phoneSignInType == 1) { // 1 验证码登录

            if (form.singleSignInFlag) {

                await SignSingleSignInCodePhone({code: form.code, phone: form.phone}).then(res => {

                    SignInSuccess(res.data)

                })

            } else {

                await SignPhoneSignInCode({
                    code: form.code,
                    tenantId: form.tenantId,
                    phone: form.phone
                }).then(res => {

                    SignInSuccess(res.data)

                })

            }

        } else { // 密码登录

            const password = PasswordRSAEncrypt(form.password) // 密码加密

            await SignPhoneSignInPassword({
                password,
                tenantId: form.tenantId,
                phone: form.phone
            }).then(res => {

                SignInSuccess(res.data)

            })

        }

    }

}
