import React from "react";
import {
    SignSignInNameSetSingleSignIn,
    SignSignInNameSetSingleSignInGetQrCodeSceneFlagSingleSignIn,
    SignSignInNameSetSingleSignInGetQrCodeUrlSingleSignIn
} from "@/api/http/SignSignInName.ts";
import SetWxModalForm from "../wx/SetWxModalForm.tsx";
import {ProFormText} from "@ant-design/pro-components";
import {PasswordRSAEncrypt} from "@/util/RsaUtil.ts";
import {
    UserSelfSetSingleSignInModalTitle,
    UserSelfUpdateSingleSignInModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";

interface ISetSingleSignInBySignInNameModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInBySignInNameModalForm) {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignSignInNameSetSingleSignInGetQrCodeUrlSingleSignIn}
                        setWxGetQrCodeSceneFlag={SignSignInNameSetSingleSignInGetQrCodeSceneFlagSingleSignIn}
                        setWx={SignSignInNameSetSingleSignIn}

                        handleFormFun={(form, qrCodeId) => {

                            form.currentPassword = PasswordRSAEncrypt(form.currentPassword!)

                        }}

                        title={props.userSelfInfo.singleSignInFlag ? UserSelfUpdateSingleSignInModalTitle : UserSelfSetSingleSignInModalTitle}

                        label={"统一登录微信扫码"}

                        formItemArr={formRef => [

                            <ProFormText.Password

                                key={"1"}

                                fieldProps={{
                                    allowClear: true,
                                }}
                                label="当前密码"
                                name="currentPassword"
                                rules={[{
                                    required: true,
                                }]}

                            />

                        ]}

        />

    </>

}