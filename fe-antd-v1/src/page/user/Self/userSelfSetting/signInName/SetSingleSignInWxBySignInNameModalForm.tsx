import React from "react";
import {
    SignSignInNameSetSingleSignInWx,
    SignSignInNameSetSingleSignInWxGetQrCodeSceneFlag,
    SignSignInNameSetSingleSignInWxGetQrCodeUrl
} from "@/api/http/SignSignInName.ts";
import SetWxModalForm from "../wx/SetWxModalForm.tsx";
import {ProFormText} from "@ant-design/pro-components";
import {PasswordRSAEncrypt} from "@/util/RsaUtil.ts";
import {
    UserSelfSetSingleSignInWxModalTitle,
    UserSelfUpdateSingleSignInWxModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import PathConstant from "@/model/constant/PathConstant.ts";

interface ISetSingleSignInWxBySignInNameModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInWxBySignInNameModalForm) {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignSignInNameSetSingleSignInWxGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignSignInNameSetSingleSignInWxGetQrCodeSceneFlag}
                        setWx={SignSignInNameSetSingleSignInWx}

                        handleFormFun={(form, qrCodeId) => {

                            form.currentPassword = PasswordRSAEncrypt(form.currentPassword!)

                        }}

                        title={props.userSelfInfo.singleSignInWxFlag ? UserSelfUpdateSingleSignInWxModalTitle : UserSelfSetSingleSignInWxModalTitle}

                        label={"统一登录微信扫码"}

                        signOutPath={PathConstant.SINGLE_SIGN_IN_PATH}

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