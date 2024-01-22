import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfUpdateSignInNameModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {PasswordRSAEncrypt} from "@/util/RsaUtil.ts";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ModalForm, ProFormText} from "@ant-design/pro-components";
import {SignSignInNameUpdateSignInName, SignSignInNameUpdateSignInNameDTO} from "@/api/http/SignSignInName.ts";

export default function () {

    return <ModalForm<SignSignInNameUpdateSignInNameDTO>

        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}

        title={UserSelfUpdateSignInNameModalTitle}

        trigger={<a>{UserSelfUpdateSignInNameModalTitle}</a>}

        onFinish={async (form) => {

            form.currentPassword = PasswordRSAEncrypt(form.currentPassword!)

            await SignSignInNameUpdateSignInName(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="newSignInName"
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

    </ModalForm>

}