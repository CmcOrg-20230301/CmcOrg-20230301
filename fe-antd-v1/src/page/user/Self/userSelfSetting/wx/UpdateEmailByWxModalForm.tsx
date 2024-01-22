import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {
    SignWxUpdateEmail,
    SignWxUpdateEmailGetQrCodeSceneFlag,
    SignWxUpdateEmailGetQrCodeUrl,
    SignWxUpdateEmailSendCode
} from "@/api/http/SignWx.ts";
import {UserSelfUpdateEmailModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import React from "react";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxUpdateEmailGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxUpdateEmailGetQrCodeSceneFlag}
                        setWx={SignWxUpdateEmail}

                        title={UserSelfUpdateEmailModalTitle}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

                                name="email"
                                fieldProps={{
                                    allowClear: true,
                                }}
                                required
                                label="新邮箱"
                                placeholder={'请输入新邮箱'}

                                rules={[
                                    {
                                        validator: Validate.email.validator
                                    }
                                ]}

                            />,

                            <ProFormCaptcha

                                key={"2"}

                                fieldProps={{
                                    maxLength: 6,
                                    allowClear: true,
                                }}

                                required
                                label="新邮箱验证码"
                                name="code"
                                placeholder={"新邮箱验证码"}
                                rules={[{validator: Validate.code.validator}]}

                                onGetCaptcha={async () => {

                                    await formRef.current?.validateFields(['email']).then(async res => {

                                        await SignWxUpdateEmailSendCode({email: res.email}).then(res => {

                                            ToastSuccess(res.msg)

                                        })

                                    })

                                }}

                            />

                        ]}

        />

    </>

}