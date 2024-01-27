import React, {useRef} from "react";
import {ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import {
    SignSignInNameSetSingleSignInPhone,
    SignSignInNameSetSingleSignInPhoneDTO,
    SignSignInNameSetSingleSignInPhoneSendCode
} from "@/api/http/SignSignInName.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {
    UserSelfSetSingleSignInPhoneModalTitle,
    UserSelfUpdateSingleSignInPhoneModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {PasswordRSAEncrypt} from "@/util/RsaUtil.ts";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import PathConstant from "@/model/constant/PathConstant.ts";

interface ISetSingleSignInPhoneBySignInNameModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInPhoneBySignInNameModalForm) {

    const formRef = useRef<ProFormInstance<SignSignInNameSetSingleSignInPhoneDTO>>();

    return <ModalForm<SignSignInNameSetSingleSignInPhoneDTO>

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

            form.currentPassword = PasswordRSAEncrypt(form.currentPassword!)

            await SignSignInNameSetSingleSignInPhone(form).then(res => {

                SignOut(undefined, PathConstant.SINGLE_SIGN_IN_PATH)
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText.Password

            fieldProps={{
                allowClear: true,
            }}
            label="当前密码"
            name="currentPassword"
            rules={[{
                required: true,
            }]}

        />

        <ProFormText

            name="phone"
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
            name="code"
            placeholder={"请输入统一登录手机验证码"}
            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['phone']).then(async res => {

                    await SignSignInNameSetSingleSignInPhoneSendCode({phone: res.phone}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

    </ModalForm>

}