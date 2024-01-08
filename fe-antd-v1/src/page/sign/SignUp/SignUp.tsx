import {useRef, useState} from "react";
import SignLayout from "@/layout/SignLayout/SignLayout";
import {LoginForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import IconSvg from '../../../../public/icon.svg'
import {Tabs} from "antd";
import {LockOutlined, SafetyCertificateOutlined, UserOutlined} from "@ant-design/icons";
import {GetAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import {SendCode, SignUpFormHandler, UseEffectSign} from "@/page/sign/SignUp/SignUpUtil";
import {Validate} from "@/util/ValidatorUtil";
import Link from "antd/lib/typography/Link";

type TSignUpType = '0' | '1'; // 注册方式

export interface ISignUpForm {

    account: string // 账号
    originPassword: string // 原始密码
    password: string // 密码
    code?: string // 验证码
    type: TSignUpType // 注册方式
    tenantId: string // 租户 id

}

const signUpTypeArr = ['登录名', '邮箱']

// 注册
export default function () {

    const tenantIdRef = useRef<string>('0');

    const [tenantName, setTenantName] = useState<string>(""); // 租户名

    UseEffectSign(tenantIdRef, setTenantName)

    const [activeKey, setActiveKey] = useState<TSignUpType>('0');
    const formRef = useRef<ProFormInstance<ISignUpForm>>();

    return (

        <SignLayout token={{colorPrimary: '#13C2C2FF'}} tenantName={tenantName}>

            <LoginForm<ISignUpForm>

                formRef={formRef}

                logo={IconSvg}

                title={tenantName + CommonConstant.SYS_NAME}

                submitter={{searchConfig: {submitText: '注册'}}}

                subTitle={CommonConstant.SYS_SUB_TITLE}

                actions={

                    <Link title={"登录已有账号"}
                          onClick={() => GetAppNav()(`${PathConstant.SIGN_IN_PATH}?tenantId=${tenantIdRef.current}`)}>登录已有账号</Link>

                }

                onFinish={async (form) => {

                    await SignUpFormHandler({...form, type: activeKey, tenantId: tenantIdRef.current})

                    return true

                }}

            >

                <Tabs activeKey={activeKey}

                      onChange={(activeKey) => {

                          formRef.current?.resetFields() // 重置表单
                          setActiveKey(activeKey as TSignUpType)

                      }}

                      items={[
                          {key: "0", label: `${signUpTypeArr[0]}注册`},
                          {key: "1", label: `${signUpTypeArr[1]}注册`}
                      ]}
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
                            validator: activeKey === '0' ? Validate.signInName.validator : Validate.email.validator
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
                            validator: Validate.password.validator
                        }
                    ]}

                />

                {
                    activeKey === '1' && (

                        <>

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

                                rules={[{validator: Validate.code.validator}]}
                                placeholder={'请输入验证码'}
                                name="code"
                                onGetCaptcha={async () => {

                                    await formRef.current?.validateFields(['account']).then(async res => {

                                        await SendCode({...res, type: activeKey, tenantId: tenantIdRef.current})

                                    })

                                }}

                            />

                        </>

                    )

                }

            </LoginForm>

        </SignLayout>

    )

}
