import {SignSignInNameUpdatePassword, SignSignInNameUpdatePasswordDTO} from "@/api/http/SignSignInName.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfUpdatePasswordTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil.ts";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ModalForm, ProFormText} from "@ant-design/pro-components";

export default function () {

    return <ModalForm<SignSignInNameUpdatePasswordDTO>

        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfUpdatePasswordTitle}

        trigger={<a>{UserSelfUpdatePasswordTitle}</a>}

        onFinish={async (form) => {

            form.oldPassword = PasswordRSAEncrypt(form.oldPassword!)
            form.originNewPassword = RSAEncryptPro(form.newPassword!)
            form.newPassword = PasswordRSAEncrypt(form.newPassword!)

            await SignSignInNameUpdatePassword(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            label="旧密码"
            placeholder={'请输入旧密码'}
            name="oldPassword"
            fieldProps={{
                allowClear: true,
            }}
            rules={[{required: true,},]}

        />

        <ProFormText

            label="新密码"
            placeholder={'请输入新密码'}
            name="newPassword"
            required
            fieldProps={{
                allowClear: true,
            }}
            rules={[{validator: Validate.password.validator}]}

        />

    </ModalForm>

}