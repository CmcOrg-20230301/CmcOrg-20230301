import {
    SignEmailUpdatePassword,
    SignEmailUpdatePasswordDTO,
    SignEmailUpdatePasswordSendCode
} from "@/api/http/SignEmail.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil.ts";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {ModalForm, ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import React from "react";
import {UserSelfUpdatePasswordTitle} from "@/page/user/Self/UserSelfSetting.tsx";

export default function () {

    return <>

        <ModalForm<SignEmailUpdatePasswordDTO>

            modalProps={{
                maskClosable: false
            }}

            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}
            title={UserSelfUpdatePasswordTitle}
            trigger={<a>{UserSelfUpdatePasswordTitle}</a>}

            onFinish={async (form) => {

                form.originNewPassword = RSAEncryptPro(form.newPassword!)
                form.newPassword = PasswordRSAEncrypt(form.newPassword!)

                await SignEmailUpdatePassword(form).then(res => {

                    SignOut()
                    ToastSuccess(res.msg)

                })

                return true

            }}

        >

            <ProFormCaptcha

                fieldProps={{
                    maxLength: 6,
                    allowClear: true,
                }}
                required
                label="邮箱验证码"
                placeholder={'请输入邮箱验证码'}
                name="code"
                rules={[{validator: Validate.code.validator}]}

                onGetCaptcha={async () => {

                    await SignEmailUpdatePasswordSendCode().then(res => {
                        ToastSuccess(res.msg)
                    })

                }}

            />

            <ProFormText

                label="新密码"
                placeholder={'请输入新密码'}
                name="newPassword"
                required
                fieldProps={{
                    allowClear: true,
                }}
                rules={[{validator: Validate.password.validator}]}

            />

        </ModalForm>

    </>

}