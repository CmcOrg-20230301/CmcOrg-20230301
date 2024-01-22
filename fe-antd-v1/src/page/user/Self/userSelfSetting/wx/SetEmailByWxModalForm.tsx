import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {UserSelfSetEmailModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import React from "react";
import {ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {
    SignWxSetEmail,
    SignWxSetEmailGetQrCodeSceneFlag,
    SignWxSetEmailGetQrCodeUrl,
    SignWxSetEmailSendCode
} from "@/api/http/SignWx.ts";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxSetEmailGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxSetEmailGetQrCodeSceneFlag}
                        setWx={SignWxSetEmail}

                        title={UserSelfSetEmailModalTitle}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

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

                            />,

                            <ProFormCaptcha

                                key={"2"}

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

                                        await SignWxSetEmailSendCode({email: res.email}).then(res => {

                                            ToastSuccess(res.msg)

                                        })

                                    })

                                }}

                            />

                        ]}

        />

    </>

}