import React from "react";
import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {ProFormCaptcha} from "@ant-design/pro-components";
import {
    SignEmailSetSingleSignIn,
    SignEmailSetSingleSignInGetQrCodeSceneFlagSingleSignIn,
    SignEmailSetSingleSignInGetQrCodeUrlSingleSignIn,
    SignEmailSetSingleSignInSendCodeEmail
} from "@/api/http/SignEmail.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {
    UserSelfSetSingleSignInModalTitle,
    UserSelfUpdateSingleSignInModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";

interface ISetSingleSignInByEmailModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInByEmailModalForm) {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignEmailSetSingleSignInGetQrCodeUrlSingleSignIn}
                        setWxGetQrCodeSceneFlag={SignEmailSetSingleSignInGetQrCodeSceneFlagSingleSignIn}
                        setWx={SignEmailSetSingleSignIn}

                        title={props.userSelfInfo.singleSignInFlag ? UserSelfUpdateSingleSignInModalTitle : UserSelfSetSingleSignInModalTitle}

                        label={"统一登录微信扫码"}

                        formItemArr={formRef => [

                            <ProFormCaptcha

                                key={"1"}

                                fieldProps={{
                                    maxLength: 6,
                                    allowClear: true,
                                }}

                                required
                                label="邮箱验证码"
                                name="emailCode"
                                placeholder={"邮箱验证码"}
                                rules={[{validator: Validate.code.validator}]}

                                onGetCaptcha={async () => {

                                    await SignEmailSetSingleSignInSendCodeEmail().then(res => {

                                        ToastSuccess(res.msg)

                                    })

                                }}

                            />

                        ]}

        />

    </>

}