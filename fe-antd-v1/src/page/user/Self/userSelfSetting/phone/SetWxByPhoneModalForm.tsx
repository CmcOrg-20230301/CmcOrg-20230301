import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {ProFormCaptcha} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import React from "react";
import {
    SignPhoneSetWx,
    SignPhoneSetWxGetQrCodeSceneFlag,
    SignPhoneSetWxGetQrCodeUrl,
    SignPhoneSetWxSendCodePhone
} from "@/api/http/SignPhone.ts";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignPhoneSetWxGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignPhoneSetWxGetQrCodeSceneFlag}
                        setWx={SignPhoneSetWx}

                        formItemArr={formRef => [

                            <ProFormCaptcha

                                key={"1"}

                                fieldProps={{
                                    maxLength: 6,
                                    allowClear: true,
                                }}

                                required
                                label="验证码"
                                name="phoneCode"
                                placeholder={"验证码"}
                                rules={[{validator: Validate.code.validator}]}

                                onGetCaptcha={async () => {

                                    await SignPhoneSetWxSendCodePhone().then(res => {

                                        ToastSuccess(res.msg)

                                    })

                                }}

                            />

                        ]}

        />

    </>

}