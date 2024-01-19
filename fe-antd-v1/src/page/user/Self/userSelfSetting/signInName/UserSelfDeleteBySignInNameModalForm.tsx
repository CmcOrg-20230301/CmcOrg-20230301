import {ModalForm, ProFormText} from "@ant-design/pro-components";
import {SignSignInNameSignDelete, SignSignInNameSignDeleteDTO} from "@/api/http/SignSignInName.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfDeleteModalTargetName, UserSelfDeleteModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {PasswordRSAEncrypt} from "@/util/RsaUtil.ts";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";

export default function () {

    return <ModalForm<SignSignInNameSignDeleteDTO>

        modalProps={{
            maskClosable: false
        }}
        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfDeleteModalTitle}
        trigger={<a className={"red3"}>{UserSelfDeleteModalTargetName}</a>}

        onFinish={async (form) => {

            const currentPassword = PasswordRSAEncrypt(form.currentPassword!)

            await SignSignInNameSignDelete({currentPassword}).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText.Password
            fieldProps={{
                allowClear: true,
            }}
            placeholder={'请输入当前密码'}
            label="当前密码"
            name="currentPassword"
            rules={[{
                required: true,
            }]}
        />

    </ModalForm>

}