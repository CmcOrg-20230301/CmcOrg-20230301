import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfSetSignInNameModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {
    SignEmailSetSignInName,
    SignEmailSetSignInNameDTO,
    SignEmailSetSignInNameSendCode
} from "@/api/http/SignEmail.ts";
import React, {useRef} from "react";

export default function () {

    const formRef = useRef<ProFormInstance<SignEmailSetSignInNameDTO>>();

    return <>

        <ModalForm<SignEmailSetSignInNameDTO>

            formRef={formRef}

            modalProps={{
                maskClosable: false
            }}

            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}

            title={UserSelfSetSignInNameModalTitle}

            trigger={<a>{UserSelfSetSignInNameModalTitle}</a>}

            onFinish={async (form) => {

                await SignEmailSetSignInName(form).then(res => {

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
                label="登录名"
                placeholder={'请输入登录名'}
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
                label="当前邮箱验证码"
                name="code"
                placeholder={"当前邮箱验证码"}
                rules={[{validator: Validate.code.validator}]}

                onGetCaptcha={async () => {

                    await formRef.current?.validateFields(['signInName']).then(async res => {

                        await SignEmailSetSignInNameSendCode({signInName: res.signInName}).then(res => {

                            ToastSuccess(res.msg)

                        })

                    })

                }}

            />

        </ModalForm>

    </>

}