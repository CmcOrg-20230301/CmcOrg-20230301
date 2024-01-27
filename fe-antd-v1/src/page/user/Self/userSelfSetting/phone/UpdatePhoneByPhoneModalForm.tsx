import {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfUpdatePhoneModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {
    SignPhoneUpdatePhone,
    SignPhoneUpdatePhoneDTO,
    SignPhoneUpdatePhoneSendCodeNew,
    SignPhoneUpdatePhoneSendCodeOld
} from "@/api/http/SignPhone.ts";

export default function () {

    const formRef = useRef<ProFormInstance<SignPhoneUpdatePhoneDTO>>();

    return <ModalForm<SignPhoneUpdatePhoneDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}

        title={UserSelfUpdatePhoneModalTitle}
        trigger={<a>{UserSelfUpdatePhoneModalTitle}</a>}

        onFinish={async (form) => {

            await SignPhoneUpdatePhone(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="newPhone"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="新手机号"
            placeholder={'请输入新手机号'}

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
            label="新手机验证码"
            name="newPhoneCode"
            placeholder={"请输入新手机验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['newPhone']).then(async res => {

                    await SignPhoneUpdatePhoneSendCodeNew({phone: res.newPhone}).then(res => {

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
            name="oldPhoneCode"
            placeholder={"请输入当前手机验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await SignPhoneUpdatePhoneSendCodeOld().then(res => {

                    ToastSuccess(res.msg)

                })

            }}

        />

    </ModalForm>

}