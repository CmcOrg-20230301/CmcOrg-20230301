import SetWxModalForm from "@/page/user/Self/userSelfSetting/SetWxModalForm.tsx";
import {
    SignPhoneUpdateWx,
    SignPhoneUpdateWxGetQrCodeSceneFlagNew,
    SignPhoneUpdateWxGetQrCodeUrlNew,
    SignPhoneUpdateWxSendCodePhone
} from "@/api/http/SignPhone.ts";
import {ProFormCaptcha} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import React from "react";
import {UserSelfUpdateWxModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignPhoneUpdateWxGetQrCodeUrlNew}
                        setWxGetQrCodeSceneFlag={SignPhoneUpdateWxGetQrCodeSceneFlagNew}
                        setWx={SignPhoneUpdateWx}

                        title={UserSelfUpdateWxModalTitle}

                        formItemArr={[

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

                                    await SignPhoneUpdateWxSendCodePhone().then(res => {

                                        ToastSuccess(res.msg)

                                    })

                                }}

                            />

                        ]}

        />

    </>

}