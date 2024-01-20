import React, {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import {
    SignSignInNameSetPhone,
    SignSignInNameSetPhoneDTO,
    SignSignInNameSetPhoneSendCode
} from "@/api/http/SignSignInName.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfSetPhoneModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";

export default function () {

    const formRef = useRef<ProFormInstance<SignSignInNameSetPhoneDTO>>();

    return <ModalForm<SignSignInNameSetPhoneDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfSetPhoneModalTitle}
        trigger={<a>{UserSelfSetPhoneModalTitle}</a>}
        onFinish={async (form) => {

            await SignSignInNameSetPhone(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="phone"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="手机号"
            placeholder={'请输入手机号'}

            rules={[
                {
                    validator: Validate.phone.validator
                }
            ]}

        />

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}

            required
            label="验证码"
            name="code"
            placeholder={"请输入验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['phone']).then(async res => {

                    await SignSignInNameSetPhoneSendCode({phone: res.phone}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

    </ModalForm>

}