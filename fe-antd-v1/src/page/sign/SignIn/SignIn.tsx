import {LockOutlined, UserOutlined,} from '@ant-design/icons';
import {LoginForm, ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from '@ant-design/pro-components';
import {Tabs} from 'antd';
import {useRef, useState} from 'react';
import IconSvg from '../../../../public/icon.svg'
import SignLayout from "@/layout/SignLayout/SignLayout";
import CommonConstant from "@/model/constant/CommonConstant";
import {SignInFormHandler} from "@/page/sign/SignIn/SignInUtil";
import {getAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import Link from 'antd/es/typography/Link';
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil";
import {
    SignEmailForgetPassword,
    SignEmailForgetPasswordDTO,
    SignEmailForgetPasswordSendCode
} from "@/api/http/SignEmail";
import {ToastSuccess} from "@/util/ToastUtil";
import {ValidatorUtil} from "@/util/ValidatorUtil";
import {UseEffectSign} from "@/page/sign/SignUp/SignUpUtil";

type TSignInType = 'account'; // 登录方式

export interface ISignInForm {

    account: string // 账号
    password: string // 密码

}

// 登录
export default function () {

    UseEffectSign()

    const [signInType, setSignInType] = useState<TSignInType>('account');

    return (

        <SignLayout>

            <LoginForm<ISignInForm>

                logo={IconSvg}
                title={CommonConstant.SYS_NAME}
                subTitle="Will have the most powerful !"

                actions={
                    <div>或者 <Link title={"注册"} onClick={() => getAppNav()(PathConstant.SIGN_UP_PATH)}>注册</Link>
                    </div>
                }

                onFinish={async (form) => {
                    await SignInFormHandler(form)
                    return true
                }}

            >

                <Tabs activeKey={signInType} onChange={(activeKey) => setSignInType(activeKey as TSignInType)}

                      items={[{key: "account", label: "账号密码登录"}]}>

                </Tabs>

                {signInType === 'account' && (

                    <>
                        <ProFormText

                            name="account"
                            fieldProps={{
                                size: 'large',
                                allowClear: true,
                                prefix: <UserOutlined/>,
                            }}
                            placeholder={"登录名/邮箱"}
                            rules={[
                                {
                                    required: true,
                                    message: '请输入登录名/邮箱'
                                },
                            ]}

                        />

                        <ProFormText.Password

                            name="password"
                            fieldProps={{
                                size: 'large',
                                allowClear: true,
                                prefix: <LockOutlined/>,
                            }}

                            placeholder={'密码'}
                            rules={[
                                {
                                    required: true,
                                    message: '请输入密码'
                                },
                            ]}

                        />

                    </>

                )}

                <div
                    className={"m-b-24 flex jc-sb"}
                >

                    {/* 忘记密码了 */}
                    <UserForgetPasswordModalForm/>

                </div>

            </LoginForm>

        </SignLayout>

    )

}

const userForgetPasswordModalTitle = "忘记密码了"

export function UserForgetPasswordModalForm() {

    const formRef = useRef<ProFormInstance<SignEmailForgetPasswordDTO>>();

    return <ModalForm<SignEmailForgetPasswordDTO>

        modalProps={{
            maskClosable: false
        }}

        formRef={formRef}
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={userForgetPasswordModalTitle}
        trigger={<a>{userForgetPasswordModalTitle}</a>}

        onFinish={async (form) => {

            const formTemp = {...form}
            formTemp.originNewPassword = RSAEncryptPro(formTemp.newPassword!)
            formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword!)

            await SignEmailForgetPassword(formTemp).then(res => {
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
            rules={[{validator: ValidatorUtil.emailValidate}]}

        />

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}

            required
            label="验证码"
            placeholder={'请输入验证码'}
            name="code"

            rules={[{validator: ValidatorUtil.codeValidate}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['email']).then(async res => {

                    await SignEmailForgetPasswordSendCode({email: res.email}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

        <ProFormText

            label="新密码"
            placeholder={'请输入新密码'}
            name="newPassword"
            required

            fieldProps={{
                allowClear: true,
            }}

            rules={[{validator: ValidatorUtil.passwordValidate}]}

        />

    </ModalForm>

}
