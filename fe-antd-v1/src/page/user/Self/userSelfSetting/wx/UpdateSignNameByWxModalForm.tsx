import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {
    SignWxUpdateSignInName,
    SignWxUpdateSignInNameGetQrCodeSceneFlag,
    SignWxUpdateSignInNameGetQrCodeUrl
} from "@/api/http/SignWx.ts";
import {UserSelfUpdateSignInNameModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import React from "react";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxUpdateSignInNameGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxUpdateSignInNameGetQrCodeSceneFlag}
                        setWx={SignWxUpdateSignInName}

                        title={UserSelfUpdateSignInNameModalTitle}

                        formItemArr={formRef => [

                            <ProFormText

                                key={"1"}

                                name="signInName"
                                fieldProps={{
                                    allowClear: true,
                                }}
                                required
                                label="新登录名"
                                placeholder={'请输入新登录名'}
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