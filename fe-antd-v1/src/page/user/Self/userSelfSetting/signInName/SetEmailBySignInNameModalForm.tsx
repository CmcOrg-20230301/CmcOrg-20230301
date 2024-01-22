import React, {useRef} from "react";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfSetEmailModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import {
    SignSignInNameSetEmail,
    SignSignInNameSetEmailDTO,
    SignSignInNameSetEmailSendCode
} from "@/api/http/SignSignInName.ts";
import {PasswordRSAEncrypt} from "@/util/RsaUtil.ts";

export default function () {

    const formRef = useRef<ProFormInstance<SignSignInNameSetEmailDTO>>();

    return <ModalForm<SignSignInNameSetEmailDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfSetEmailModalTitle}
        trigger={<a>{UserSelfSetEmailModalTitle}</a>}

        onFinish={async (form) => {

            form.currentPassword = PasswordRSAEncrypt(form.currentPassword!)

            await SignSignInNameSetEmail(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText.Password

            fieldProps={{
                allowClear: true,
            }}
            label="当前密码"
            name="currentPassword"
            rules={[{
                required: true,
            }]}

        />

        <ProFormText

            name="email"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="邮箱"
            placeholder={'请输入邮箱'}

            rules={[
                {
                    validator: Validate.email.validator
                }
            ]}

        />

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}

            required
            label="邮箱验证码"
            name="code"
            placeholder={"邮箱验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['email']).then(async res => {

                    await SignSignInNameSetEmailSendCode({email: res.email}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

    </ModalForm>

}