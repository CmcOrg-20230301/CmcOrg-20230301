import {useRef, useState} from "react";
import SignLayout from "@/layout/SignLayout/SignLayout";
import {LoginForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import IconSvg from '../../../../public/icon.svg'
import {Tabs} from "antd";
import {LockOutlined, SafetyCertificateOutlined, UserOutlined} from "@ant-design/icons/lib";
import {getAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import {SendCode, SignUpFormHandler, UseEffectSign} from "@/page/sign/SignUp/SignUpUtil";
import {ValidatorUtil} from "@/util/ValidatorUtil";
import Link from "antd/lib/typography/Link";

type TSignUpType = '0' | '1'; // 注册方式

export interface ISignUpForm {

    account: string // 账号
    originPassword: string // 原始密码
    password: string // 密码
    code?: string // 验证码
    type: TSignUpType // 注册方式

}

const signUpTypeArr = ['登录名', '邮箱']

// 注册
export default function () {

    UseEffectSign()

    const [activeKey, setActiveKey] = useState<TSignUpType>('0');
    const formRef = useRef<ProFormInstance<ISignUpForm>>();

    return (

        <SignLayout token={{colorPrimary: '#13C2C2FF'}}>

            <LoginForm<ISignUpForm>

                formRef={formRef}
                logo={IconSvg}
                title={CommonConstant.SYS_NAME}
                submitter={{searchConfig: {submitText: '注册'}}}
                subTitle="Will have the most powerful !"
                actions={
                    <Link title={"登录已有账号"} onClick={() => getAppNav()(PathConstant.SIGN_IN_PATH)}>登录已有账号</Link>
                }

                onFinish={async (form) => {

                    await SignUpFormHandler({...form, type: activeKey})
                    return true

                }}

            >

                <Tabs activeKey={activeKey}

                      onChange={(activeKey) => {

                          formRef.current?.resetFields() // 重置表单
                          setActiveKey(activeKey as TSignUpType)

                      }}

                      items={[{key: "0", label: `${signUpTypeArr[0]}注册`}, {key: "1", label: `${signUpTypeArr[1]}注册`}]}
                >

                </Tabs>

                <ProFormText

                    name="account"
                    fieldProps={{
                        size: 'large',
                        allowClear: true,
                        prefix: <UserOutlined className={'prefixIcon'}/>,
                    }}

                    placeholder={signUpTypeArr[Number(activeKey)]}

                    rules={[
                        {
                            validator: ValidatorUtil[activeKey === '0' ? 'signInNameValidate' : 'emailValidate']
                        }
                    ]}

                />

                <ProFormText.Password

                    name="password"
                    fieldProps={{
                        size: 'large',
                        allowClear: true,
                        prefix: <LockOutlined className={'prefixIcon'}/>,
                    }}
                    placeholder={'密码'}
                    rules={[
                        {
                            validator: ValidatorUtil.passwordValidate
                        }
                    ]}

                />

                {
                    activeKey === '1' && (<>

                        <ProFormCaptcha

                            fieldProps={{
                                size: 'large',
                                maxLength: 6,
                                allowClear: true,
                                prefix: <SafetyCertificateOutlined/>,
                            }}
                            captchaProps={{
                                size: 'large',
                            }}

                            rules={[{validator: ValidatorUtil.codeValidate}]}
                            placeholder={'请输入验证码'}
                            name="code"
                            onGetCaptcha={async () => {

                                await formRef.current?.validateFields(['account']).then(async res => {

                                    await SendCode({...res, type: activeKey})

                                })

                            }}

                        />

                    </>)

                }

            </LoginForm>

        </SignLayout>

    )

}
