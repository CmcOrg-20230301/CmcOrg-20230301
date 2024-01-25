import React, {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import {
    SignPhoneUpdateSignInName,
    SignPhoneUpdateSignInNameDTO,
    SignPhoneUpdateSignInNameSendCode
} from "@/api/http/SignPhone.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfUpdateSignInNameModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";

export default function () {

    const formRef = useRef<ProFormInstance<SignPhoneUpdateSignInNameDTO>>();

    return <>

        <ModalForm<SignPhoneUpdateSignInNameDTO>

            formRef={formRef}

            modalProps={{
                maskClosable: false
            }}

            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}

            title={UserSelfUpdateSignInNameModalTitle}

            trigger={<a>{UserSelfUpdateSignInNameModalTitle}</a>}

            onFinish={async (form) => {

                await SignPhoneUpdateSignInName(form).then(res => {

                    SignOut()
                    ToastSuccess(res.msg)

                })

                return true

            }}

        >

            <ProFormText

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

            <ProFormCaptcha

                fieldProps={{
                    maxLength: 6,
                    allowClear: true,
                }}

                required
                label="手机验证码"
                name="code"
                placeholder={"手机验证码"}
                rules={[{validator: Validate.code.validator}]}

                onGetCaptcha={async () => {

                    await formRef.current?.validateFields(['signInName']).then(async res => {

                        await SignPhoneUpdateSignInNameSendCode({signInName: res.signInName}).then(res => {

                            ToastSuccess(res.msg)

                        })

                    })

                }}

            />

        </ModalForm>

    </>

}