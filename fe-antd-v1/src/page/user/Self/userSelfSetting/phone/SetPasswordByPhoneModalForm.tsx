import {ModalForm, ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfSetPasswordTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil.ts";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import React from "react";
import {SignPhoneSetPassword, SignPhoneSetPasswordDTO, SignPhoneSetPasswordSendCode} from "@/api/http/SignPhone.ts";

export default function () {

    return <>

        <ModalForm<SignPhoneSetPasswordDTO>

            modalProps={{
                maskClosable: false
            }}

            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}
            title={UserSelfSetPasswordTitle}
            trigger={<a>{UserSelfSetPasswordTitle}</a>}

            onFinish={async (form) => {

                form.originNewPassword = RSAEncryptPro(form.newPassword!)
                form.newPassword = PasswordRSAEncrypt(form.newPassword!)

                await SignPhoneSetPassword(form).then(res => {

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
                label="当前手机验证码"
                placeholder={'请输入当前手机验证码'}
                name="code"
                rules={[{validator: Validate.code.validator}]}

                onGetCaptcha={async () => {

                    await SignPhoneSetPasswordSendCode().then(res => {
                        ToastSuccess(res.msg)
                    })

                }}

            />

            <ProFormText

                label="密码"
                placeholder={'请输入密码'}
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