import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {
    SignWxUpdatePassword,
    SignWxUpdatePasswordGetQrCodeSceneFlag,
    SignWxUpdatePasswordGetQrCodeUrl
} from "@/api/http/SignWx.ts";
import {UserSelfUpdatePasswordTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil.ts";
import {ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import React from "react";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxUpdatePasswordGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxUpdatePasswordGetQrCodeSceneFlag}
                        setWx={SignWxUpdatePassword}

                        title={UserSelfUpdatePasswordTitle}

                        handleFormFun={(form, qrCodeId) => {

                            form.originNewPassword = RSAEncryptPro(form.newPassword!)
                            form.newPassword = PasswordRSAEncrypt(form.newPassword!)

                        }}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

                                label="新密码"
                                placeholder={'请输入新密码'}
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