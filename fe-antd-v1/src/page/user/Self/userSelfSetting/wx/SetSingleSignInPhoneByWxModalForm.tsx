import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {
    SignWxSetSingleSignInPhone,
    SignWxSetSingleSignInPhoneGetQrCodeSceneFlagCurrent,
    SignWxSetSingleSignInPhoneGetQrCodeUrlCurrent,
    SignWxSetSingleSignInPhoneSendCode
} from "@/api/http/SignWx.ts";
import {
    UserSelfSetSingleSignInPhoneModalTitle,
    UserSelfUpdateSingleSignInPhoneModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import React from "react";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import PathConstant from "@/model/constant/PathConstant.ts";

interface ISetSingleSignInPhoneByWxModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInPhoneByWxModalForm) {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxSetSingleSignInPhoneGetQrCodeUrlCurrent}
                        setWxGetQrCodeSceneFlag={SignWxSetSingleSignInPhoneGetQrCodeSceneFlagCurrent}
                        setWx={SignWxSetSingleSignInPhone}

                        title={props.userSelfInfo.singleSignInPhoneFlag ? UserSelfUpdateSingleSignInPhoneModalTitle : UserSelfSetSingleSignInPhoneModalTitle}

                        signOutPath={PathConstant.SINGLE_SIGN_IN_PATH}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

                                name="singleSignInPhone"
                                fieldProps={{
                                    allowClear: true,
                                }}
                                required
                                label="统一登录手机号"
                                placeholder={'请输入统一登录手机号'}

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
                                label="统一登录手机验证码"
                                name="singleSignInPhoneCode"
                                placeholder={"请输入统一登录手机验证码"}
                                rules={[{validator: Validate.code.validator}]}

                                onGetCaptcha={async () => {

                                    await formRef.current?.validateFields(['singleSignInPhone']).then(async res => {

                                        await SignWxSetSingleSignInPhoneSendCode({singleSignInPhone: res.singleSignInPhone}).then(res => {

                                            ToastSuccess(res.msg)

                                        })

                                    })

                                }}

                            />

                        ]}

        />

    </>

}