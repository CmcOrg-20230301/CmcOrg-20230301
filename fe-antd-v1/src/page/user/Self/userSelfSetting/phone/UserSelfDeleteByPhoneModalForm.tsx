import {ModalForm, ProFormCaptcha} from "@ant-design/pro-components";
import {NotBlankCodeDTO} from "@/api/http/SignEmail.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {UserSelfDeleteModalTargetName, UserSelfDeleteModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {SignOut} from "@/util/UserUtil.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {Validate} from "@/util/ValidatorUtil.ts";
import {SignPhoneSignDelete, SignPhoneSignDeleteSendCode} from "@/api/http/SignPhone.ts";

export default function () {

    return <>

        <ModalForm<NotBlankCodeDTO>

            modalProps={{
                maskClosable: false
            }}
            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}
            title={UserSelfDeleteModalTitle}
            trigger={<a className={"red3"}>{UserSelfDeleteModalTargetName}</a>}

            onFinish={async (form) => {

                await SignPhoneSignDelete({code: form.code}).then(res => {

                    SignOut()
                    ToastSuccess(res.msg)

                })

                return true

            }}

        >

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

                    await SignPhoneSignDeleteSendCode().then(res => {

                        ToastSuccess(res.msg)

                    })

                }}

            />

        </ModalForm>

    </>

}