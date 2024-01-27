import {
    SignEmailUpdateEmail,
    SignEmailUpdateEmailDTO,
    SignEmailUpdateEmailSendCodeNew,
    SignEmailUpdateEmailSendCodeOld
} from "@/api/http/SignEmail.ts";
import {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {UserSelfUpdateEmailModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {Validate} from "@/util/ValidatorUtil.ts";

export default function () {

    const formRef = useRef<ProFormInstance<SignEmailUpdateEmailDTO>>();

    return <ModalForm<SignEmailUpdateEmailDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}

        title={UserSelfUpdateEmailModalTitle}
        trigger={<a>{UserSelfUpdateEmailModalTitle}</a>}

        onFinish={async (form) => {

            await SignEmailUpdateEmail(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="newEmail"
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
            label="新邮箱验证码"
            name="newEmailCode"
            placeholder={"请输入新邮箱验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['newEmail']).then(async res => {

                    await SignEmailUpdateEmailSendCodeNew({email: res.newEmail}).then(res => {

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
            label="当前邮箱验证码"
            name="oldEmailCode"
            placeholder={"请输入当前邮箱验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await SignEmailUpdateEmailSendCodeOld().then(res => {

                    ToastSuccess(res.msg)

                })

            }}

        />

    </ModalForm>

}