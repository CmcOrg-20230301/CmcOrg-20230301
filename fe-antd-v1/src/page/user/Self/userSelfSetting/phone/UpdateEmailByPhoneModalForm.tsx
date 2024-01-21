import {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import {
    SignPhoneUpdateEmail,
    SignPhoneUpdateEmailDTO,
    SignPhoneUpdateEmailSendCodeEmail,
    SignPhoneUpdateEmailSendCodePhone
} from "@/api/http/SignPhone.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfUpdateEmailModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";

export default function () {

    const formRef = useRef<ProFormInstance<SignPhoneUpdateEmailDTO>>();

    return <ModalForm<SignPhoneUpdateEmailDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}

        title={UserSelfUpdateEmailModalTitle}
        trigger={<a>{UserSelfUpdateEmailModalTitle}</a>}

        onFinish={async (form) => {

            await SignPhoneUpdateEmail(form).then(res => {

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
            label="新邮箱"
            placeholder={'请输入新邮箱'}

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

                    await SignPhoneUpdateEmailSendCodeEmail({email: res.email}).then(res => {

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
            label="手机验证码"
            name="phoneCode"
            placeholder={"请输入手机验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['email']).then(async res => {

                    await SignPhoneUpdateEmailSendCodePhone({email: res.email}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

    </ModalForm>

}