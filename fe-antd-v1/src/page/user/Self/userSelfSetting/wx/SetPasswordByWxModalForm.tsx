import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {
    SignWxSetPassword,
    SignWxSetPasswordGetQrCodeSceneFlag,
    SignWxSetPasswordGetQrCodeUrl
} from "@/api/http/SignWx.ts";
import {UserSelfSetPasswordTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import React from "react";
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil.ts";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxSetPasswordGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxSetPasswordGetQrCodeSceneFlag}
                        setWx={SignWxSetPassword}

                        title={UserSelfSetPasswordTitle}

                        handleFormFun={(form, qrCodeId) => {

                            form.originNewPassword = RSAEncryptPro(form.newPassword!)
                            form.newPassword = PasswordRSAEncrypt(form.newPassword!)

                        }}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

                                label="密码"
                                placeholder={'请输入密码'}
                                name="newPassword"
                                required
                                fieldProps={{
                                    allowClear: true,
                                }}
                                rules={[{validator: Validate.password.validator}]}

                            />

                        ]}

        />

    </>

}