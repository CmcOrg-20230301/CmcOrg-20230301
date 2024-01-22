import React from "react";
import {
    SignSignInNameSetWx,
    SignSignInNameSetWxGetQrCodeSceneFlag,
    SignSignInNameSetWxGetQrCodeUrl
} from "@/api/http/SignSignInName.ts";
import SetWxModalForm from "../wx/SetWxModalForm.tsx";
import {ProFormText} from "@ant-design/pro-components";
import {PasswordRSAEncrypt} from "@/util/RsaUtil.ts";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignSignInNameSetWxGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignSignInNameSetWxGetQrCodeSceneFlag}
                        setWx={SignSignInNameSetWx}

                        handleFormFun={(form, qrCodeId) => {

                            form.currentPassword = PasswordRSAEncrypt(form.currentPassword!)

                        }}

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