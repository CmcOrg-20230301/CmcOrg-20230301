import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
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

                        label={"新微信扫码"}

                        formItemArr={formRef => [

                            <ProFormCaptcha

                                key={"1"}

                                fieldProps={{
                                    maxLength: 6,
                                    allowClear: true,
                                }}

                                required
                                label="当前手机验证码"
                                name="phoneCode"
                                placeholder={"当前手机验证码"}
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