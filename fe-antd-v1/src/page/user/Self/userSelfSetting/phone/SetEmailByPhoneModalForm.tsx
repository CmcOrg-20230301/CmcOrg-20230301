import {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfSetEmailModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {
    SignPhoneSetEmail,
    SignPhoneSetEmailDTO,
    SignPhoneSetEmailSendCodeEmail,
    SignPhoneSetEmailSendCodePhone
} from "@/api/http/SignPhone.ts";

export default function () {

    const formRef = useRef<ProFormInstance<SignPhoneSetEmailDTO>>();

    return <ModalForm<SignPhoneSetEmailDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}

        title={UserSelfSetEmailModalTitle}
        trigger={<a>{UserSelfSetEmailModalTitle}</a>}

        onFinish={async (form) => {

            await SignPhoneSetEmail(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="email"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="邮箱"
            placeholder={'请输入邮箱'}

            rules={[
                {
                    validator: Validate.email.validator
                }
            ]}

        />

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}

            required
            label="邮箱验证码"
            name="emailCode"
            placeholder={"请输入邮箱验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['email']).then(async res => {

                    await SignPhoneSetEmailSendCodeEmail({email: res.email}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}

            required
            label="当前手机验证码"
            name="phoneCode"
            placeholder={"请输入当前手机验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['email']).then(async res => {

                    await SignPhoneSetEmailSendCodePhone({email: res.email}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

    </ModalForm>

}