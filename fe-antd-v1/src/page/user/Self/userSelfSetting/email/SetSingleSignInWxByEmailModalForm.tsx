import React from "react";
import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {ProFormCaptcha} from "@ant-design/pro-components";
import {
    SignEmailSetSingleSignInWx,
    SignEmailSetSingleSignInWxGetQrCodeSceneFlag,
    SignEmailSetSingleSignInWxGetQrCodeUrl,
    SignEmailSetSingleSignInWxSendCode
} from "@/api/http/SignEmail.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {
    UserSelfSetSingleSignInWxModalTitle,
    UserSelfUpdateSingleSignInWxModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import PathConstant from "@/model/constant/PathConstant.ts";

interface ISetSingleSignInWxByEmailModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInWxByEmailModalForm) {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignEmailSetSingleSignInWxGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignEmailSetSingleSignInWxGetQrCodeSceneFlag}
                        setWx={SignEmailSetSingleSignInWx}

                        title={props.userSelfInfo.singleSignInWxFlag ? UserSelfUpdateSingleSignInWxModalTitle : UserSelfSetSingleSignInWxModalTitle}

                        label={"统一登录微信扫码"}

                        signOutPath={PathConstant.SINGLE_SIGN_IN_PATH}

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

                                    await SignEmailSetSingleSignInWxSendCode().then(res => {

                                        ToastSuccess(res.msg)

                                    })

                                }}

                            />

                        ]}

        />

    </>

}