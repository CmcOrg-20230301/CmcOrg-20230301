import {useMemo, useRef, useState} from "react";
import SignLayout from "@/layout/SignLayout/SignLayout";
import {LoginForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import IconSvg from '../../../../public/icon.svg'
import {Tabs} from "antd";
import {LockOutlined, SafetyCertificateOutlined} from "@ant-design/icons";
import {GetAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import {SendCode, SignUpFormHandler, UseEffectSign} from "@/page/sign/SignUp/SignUpUtil";
import {Validate} from "@/util/ValidatorUtil";
import Link from "antd/lib/typography/Link";
import {useAppSelector} from "@/store";
import {MyUseState} from "@/util/HookUtil.ts";
import {SysSignConfigurationVO, SysTenantGetConfigurationById} from "@/api/http/SysTenant.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {SetSysSignConfigurationVOCallBack} from "@/page/sign/SignIn/SignIn.tsx";
import type {Tab} from "rc-tabs/lib/interface";
import {ISysSignTypeItemEnum, SysSignTypeEnum, SysSignTypeEnumMap} from "@/model/enum/SysSignTypeEnum.tsx";

export interface ISignUpForm {

    account: string // 账号
    originPassword: string // 原始密码
    password: string // 密码
    code: string // 验证码
    signUpType: string // 注册方式
    tenantId: string // 租户 id

}

// 注册
export default function () {

    const tenantIdRef = useRef<string>(CommonConstant.TOP_TENANT_ID_STR);

    const tenantManageName = useAppSelector(state => state.common.tenantManageName);

    UseEffectSign(tenantIdRef)

    const formRef = useRef<ProFormInstance<ISignUpForm>>();

    // 展示的注册方式
    const [tabItemArr, setTabItemArr] = useState<Tab[]>([]);

    const [signUpType, setSignUpType, signUpTypeRef] = MyUseState(useState<string>(""));

    const [sysSignConfigurationVO, setSysSignConfigurationVO, sysSignConfigurationVORef] =

        MyUseState(
            useState<SysSignConfigurationVO>({}),

            newState => {

                SetSysSignConfigurationVOCallBack(signUpTypeRef.current, setSignUpType, setTabItemArr, undefined, 2)(newState)

            }
        )

    UseEffectSign(tenantIdRef, () => {

        // 为了触发：callBack
        setSysSignConfigurationVO(JSON.parse(localStorage.getItem(LocalStorageKey.SYS_SIGN_CONFIGURATION_VO) || "{}"))

        // 租户相关配置
        SysTenantGetConfigurationById({value: tenantIdRef.current}).then(res => {

            setSysSignConfigurationVO(res.data)

        })

    })

    const sysSignUpTypeEnum: ISysSignTypeItemEnum | undefined = useMemo(() => {

        return SysSignTypeEnumMap.get(signUpType)

    }, [signUpType]);

    return (

        <SignLayout token={{colorPrimary: '#13C2C2FF'}} tenantManageName={tenantManageName}>

            <LoginForm<ISignUpForm>

                formRef={formRef}

                logo={IconSvg}

                title={tenantManageName}

                submitter={{searchConfig: {submitText: '注册'}}}

                subTitle={CommonConstant.SYS_SUB_TITLE}

                actions={

                    <Link title={"登录已有账号"}
                          onClick={() => GetAppNav()(`${PathConstant.SIGN_IN_PATH}?tenantId=${tenantIdRef.current}`)}>登录已有账号</Link>

                }

                onFinish={async (form) => {

                    await SignUpFormHandler({...form, signUpType, tenantId: tenantIdRef.current})

                    return true

                }}

            >

                <Tabs activeKey={signUpType}

                      onChange={(activeKey) => {

                          formRef.current?.resetFields() // 重置表单
                          setSignUpType(activeKey)

                      }}

                      items={tabItemArr}

                >

                </Tabs>

                <ProFormText

                    name="account"

                    fieldProps={{
                        size: 'large',
                        allowClear: true,
                        prefix: sysSignUpTypeEnum?.prefix,
                    }}

                    placeholder={sysSignUpTypeEnum?.placeholder}

                    rules={[
                        {
                            validator: sysSignUpTypeEnum?.validator
                        }
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
                            validator: Validate.password.validator
                        }
                    ]}

                />

                {

                    (signUpType === SysSignTypeEnum.Email.code || signUpType === SysSignTypeEnum.Phone.code) && (

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

                                        await SendCode({...res, signUpType, tenantId: tenantIdRef.current})

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
