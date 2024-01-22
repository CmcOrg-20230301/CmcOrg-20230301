import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {
    SignWxSetPhone,
    SignWxSetPhoneGetQrCodeSceneFlag,
    SignWxSetPhoneGetQrCodeUrl,
    SignWxSetPhoneSendCode
} from "@/api/http/SignWx.ts";
import {UserSelfSetPhoneModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import React from "react";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxSetPhoneGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxSetPhoneGetQrCodeSceneFlag}
                        setWx={SignWxSetPhone}

                        title={UserSelfSetPhoneModalTitle}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

                                name="phone"
                                fieldProps={{
                                    allowClear: true,
                                }}
                                required
                                label="手机号"
                                placeholder={'请输入手机号'}

                                rules={[
                                    {
                                        validator: Validate.phone.validator
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
                                label="手机验证码"
                                name="code"
                                placeholder={"请输入手机验证码"}
                                rules={[{validator: Validate.code.validator}]}

                                onGetCaptcha={async () => {

                                    await formRef.current?.validateFields(['phone']).then(async res => {

                                        await SignWxSetPhoneSendCode({phone: res.phone}).then(res => {

                                            ToastSuccess(res.msg)

                                        })

                                    })

                                }}

                            />

                        ]}

        />

    </>

}