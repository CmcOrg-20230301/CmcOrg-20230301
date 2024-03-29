import React, {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {
    UserSelfSetSingleSignInPhoneModalTitle,
    UserSelfUpdateSingleSignInPhoneModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {
    SignEmailSetSingleSignInPhone,
    SignEmailSetSingleSignInPhoneDTO,
    SignEmailSetSingleSignInPhoneSendCode,
    SignEmailSetSingleSignInPhoneSendCodeCurrent
} from "@/api/http/SignEmail.ts";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import PathConstant from "@/model/constant/PathConstant.ts";

interface ISetSingleSignInPhoneByEmailModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInPhoneByEmailModalForm) {

    const formRef = useRef<ProFormInstance<SignEmailSetSingleSignInPhoneDTO>>();

    return <ModalForm<SignEmailSetSingleSignInPhoneDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={props.userSelfInfo.singleSignInPhoneFlag ? UserSelfUpdateSingleSignInPhoneModalTitle : UserSelfSetSingleSignInPhoneModalTitle}
        trigger={
            <a>{props.userSelfInfo.singleSignInPhoneFlag ? UserSelfUpdateSingleSignInPhoneModalTitle : UserSelfSetSingleSignInPhoneModalTitle}</a>}
        onFinish={async (form) => {

            await SignEmailSetSingleSignInPhone(form).then(res => {

                SignOut(undefined, PathConstant.SINGLE_SIGN_IN_PATH)
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="singleSignInPhone"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="统一登录手机号"
            placeholder={'请输入统一登录手机号'}

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
            label="统一登录手机验证码"
            name="singleSignInPhoneCode"
            placeholder={"请输入统一登录手机验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['singleSignInPhone']).then(async res => {

                    await SignEmailSetSingleSignInPhoneSendCode({phone: res.singleSignInPhone}).then(res => {

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
            name="currentEmailCode"
            placeholder={"请输入当前邮箱验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await SignEmailSetSingleSignInPhoneSendCodeCurrent().then(res => {

                    ToastSuccess(res.msg)

                })

            }}

        />

    </ModalForm>

}