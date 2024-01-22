import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import React from "react";
import {
    SignWxSetSignInName,
    SignWxSetSignInNameGetQrCodeSceneFlag,
    SignWxSetSignInNameGetQrCodeUrl
} from "@/api/http/SignWx.ts";
import {UserSelfSetSignInNameModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxSetSignInNameGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxSetSignInNameGetQrCodeSceneFlag}
                        setWx={SignWxSetSignInName}

                        title={UserSelfSetSignInNameModalTitle}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

                                name="signInName"
                                fieldProps={{
                                    allowClear: true,
                                }}
                                required
                                label="登录名"
                                placeholder={'请输入登录名'}
                                rules={[
                                    {
                                        validator: Validate.signInName.validator
                                    }
                                ]}

                            />

                        ]}

        />

    </>

}