import React from "react";
import SetWxModalForm from "@/page/user/Self/userSelfSetting/SetWxModalForm.tsx";
import {ProFormCaptcha} from "@ant-design/pro-components";
import {
    SignEmailSetWx,
    SignEmailSetWxGetQrCodeSceneFlag,
    SignEmailSetWxGetQrCodeUrl,
    SignEmailSetWxSendCode
} from "@/api/http/SignEmail.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignEmailSetWxGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignEmailSetWxGetQrCodeSceneFlag}
                        setWx={SignEmailSetWx}

                        formItemArr={[

                            <ProFormCaptcha

                                key={"1"}

                                fieldProps={{
                                    maxLength: 6,
                                    allowClear: true,
                                }}

                                required
                                label="验证码"
                                name="emailCode"
                                placeholder={"验证码"}
                                rules={[{validator: Validate.code.validator}]}

                                onGetCaptcha={async () => {

                                    await SignEmailSetWxSendCode().then(res => {

                                        ToastSuccess(res.msg)

                                    })

                                }}

                            />

                        ]}

        />

    </>

}