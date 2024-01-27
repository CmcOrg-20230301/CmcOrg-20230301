import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {ProFormCaptcha} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import React from "react";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import {
    UserSelfSetSingleSignInWxModalTitle,
    UserSelfUpdateSingleSignInWxModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {
    SignPhoneSetSingleSignInWx,
    SignPhoneSetSingleSignInWxGetQrCodeSceneFlag,
    SignPhoneSetSingleSignInWxGetQrCodeUrl,
    SignPhoneSetSingleSignInWxSendCode
} from "@/api/http/SignPhone.ts";

interface ISetSingleSignInByPhoneModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInByPhoneModalForm) {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignPhoneSetSingleSignInWxGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignPhoneSetSingleSignInWxGetQrCodeSceneFlag}
                        setWx={SignPhoneSetSingleSignInWx}

                        title={props.userSelfInfo.singleSignInWxFlag ? UserSelfUpdateSingleSignInWxModalTitle : UserSelfSetSingleSignInWxModalTitle}

                        label={"统一登录微信扫码"}

                        formItemArr={formRef => [

                            <ProFormCaptcha

                                key={"1"}

                                fieldProps={{
                                    maxLength: 6,
                                    allowClear: true,
                                }}

                                required
                                label="手机验证码"
                                name="phoneCode"
                                placeholder={"手机验证码"}
                                rules={[{validator: Validate.code.validator}]}

                                onGetCaptcha={async () => {

                                    await SignPhoneSetSingleSignInWxSendCode().then(res => {

                                        ToastSuccess(res.msg)

                                    })

                                }}

                            />

                        ]}

        />

    </>

}