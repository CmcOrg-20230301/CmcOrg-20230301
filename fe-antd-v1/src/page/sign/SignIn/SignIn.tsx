import {LockOutlined, SafetyCertificateOutlined,} from '@ant-design/icons';
import {LoginForm, ModalForm, ProFormCaptcha, ProFormInstance, ProFormText} from '@ant-design/pro-components';
import {Image, Tabs} from 'antd';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import IconSvg from '../../../../public/icon.svg'
import SignLayout from "@/layout/SignLayout/SignLayout";
import CommonConstant from "@/model/constant/CommonConstant";
import {SignInFormHandler, SignInSuccess} from "@/page/sign/SignIn/SignInUtil";
import {GetAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import Link from 'antd/es/typography/Link';
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil";
import {
    SignEmailForgetPassword,
    SignEmailForgetPasswordDTO,
    SignEmailForgetPasswordSendCode
} from "@/api/http/SignEmail";
import {ToastSuccess} from "@/util/ToastUtil";
import {Validate} from "@/util/ValidatorUtil";
import {UseEffectSign} from "@/page/sign/SignUp/SignUpUtil";
import {GetTenantId} from "@/util/CommonUtil";
import {useAppSelector} from "@/store";
import {SysSignConfigurationVO, SysTenantGetConfigurationById} from "@/api/http/SysTenant.ts";
import {SignWxSignInByQrCodeId, SignWxSignInGetQrCodeUrl} from "@/api/http/SignWx.ts";
import {MyUseState} from "@/util/HookUtil.ts";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {ISysSignTypeItemEnum, SysSignTypeEnum, SysSignTypeEnumMap, TSignType} from "@/model/enum/SysSignTypeEnum.tsx";
import type {Tab} from "rc-tabs/lib/interface";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {
    SignPhoneForgetPassword,
    SignPhoneForgetPasswordDTO,
    SignPhoneForgetPasswordSendCode,
    SignPhoneSignInSendCode
} from "@/api/http/SignPhone.ts";

export interface ISignInForm {

    signInType: string; // 登录类型
    password: string; // 密码
    phone: string; // 手机号
    tenantId: string;  // 租户 id
    phoneSignInType: 1 | 2; // 手机号登录方式：1 验证码登录 2 密码登录
    account: string // 登录名/邮箱
    code: string // 验证码

    singleSignInFlag?: boolean // 是否是统一登录

}

/**
 * 设置：租户相关配置时的回调方法
 *
 * @param signType 1 登录 2 注册
 */
export function SetSysSignConfigurationVOCallBack(signInType: string, setSignInType: (value: string) => void, setTabItemArr: (value: (((prevState: Tab[]) => Tab[]) | Tab[])) => void, setAccountPlaceholder: ((value: (((prevState: string) => string) | string)) => void) | undefined, signType: TSignType, savaFlag: boolean = true) {

    return sysSignConfigurationVO => {

        const tabItemArrTemp: Tab[] = []

        let accountPlaceholderTemp = ""

        const signInFlag = signType === 1; // 是否是：登录

        let signInTypeExistFlag = false; // 登录方式是否存在

        SysSignTypeEnumMap.forEach(item => {

            if (!item.showFlag(sysSignConfigurationVO, signType)) {
                return
            }

            if (signInFlag) { // 如果是：登录

                if (item.signInAddAccountFlag) {

                    if (accountPlaceholderTemp) {

                        accountPlaceholderTemp = accountPlaceholderTemp + "\\" + item.placeholder

                    } else {

                        accountPlaceholderTemp = item.placeholder!

                        tabItemArrTemp.push({
                            key: SysSignTypeEnum.SignInName.code!,
                            label: SysSignTypeEnum.SignInName.name
                        })

                        if (!signInTypeExistFlag && signInType && signInType === SysSignTypeEnum.SignInName.code) {
                            signInTypeExistFlag = true
                        }

                    }

                } else {

                    tabItemArrTemp.push({key: item.code!, label: item.name})

                    if (!signInTypeExistFlag && signInType && signInType === item.code) {
                        signInTypeExistFlag = true
                    }

                }

            } else { // 如果是：注册

                tabItemArrTemp.push({key: item.code!, label: item.placeholder + "注册"})

                if (!signInTypeExistFlag && signInType && signInType === item.code) {
                    signInTypeExistFlag = true
                }

            }


        })

        if (signInType) {

            if (!signInTypeExistFlag) {

                if (tabItemArrTemp.length) {
                    setSignInType(tabItemArrTemp[0].key)
                }

            }

        } else {

            if (tabItemArrTemp.length) {
                setSignInType(tabItemArrTemp[0].key)
            }

        }

        setTabItemArr(tabItemArrTemp)

        if (setAccountPlaceholder) {
            setAccountPlaceholder(accountPlaceholderTemp)
        }

        if (savaFlag) {

            localStorage.setItem(LocalStorageKey.SYS_SIGN_CONFIGURATION_VO, JSON.stringify(sysSignConfigurationVO))

        }

    };

}

// 登录
export default function () {

    const formRef = useRef<ProFormInstance<ISignInForm>>();

    const tenantIdRef = useRef<string>(CommonConstant.TOP_TENANT_ID_STR); // 租户 id

    const tenantManageName = useAppSelector(state => state.common.tenantManageName);

    // 展示的登录方式
    const [tabItemArr, setTabItemArr] = useState<Tab[]>([]);

    const [signInType, setSignInType, signInTypeRef] = MyUseState(useState<string>(localStorage.getItem(LocalStorageKey.SIGN_IN_TYPE) || ""));

    const [accountPlaceholder, setAccountPlaceholder] = useState<string>("");

    const [sysSignConfigurationVO, setSysSignConfigurationVO, sysSignConfigurationVORef] =

        MyUseState(
            useState<SysSignConfigurationVO>({}),

            newState => {

                SetSysSignConfigurationVOCallBack(signInTypeRef.current, setSignInType, setTabItemArr, setAccountPlaceholder, 1)(newState)

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

    const sysSignInTypeEnum: ISysSignTypeItemEnum | undefined = useMemo(() => {

        const sysSignTypeItemEnum = SysSignTypeEnumMap.get(signInType) || SysSignTypeEnum.SignInName;

        localStorage.setItem(LocalStorageKey.SIGN_IN_TYPE, sysSignTypeItemEnum.code!)

        return sysSignTypeItemEnum

    }, [signInType]);

    const signWxSignInGetQrCodeUrlFlagRef = useRef<boolean>(false);

    useEffect(() => {

        const interval = setInterval(() => {

            if (sysSignConfigurationVORef.current.wxQrCodeSignUp) { // 如果：允许微信登录

                // 二维码过期时间
                const expireTs = Number(sysSignConfigurationVORef.current.wxQrCodeSignUp.expireTs || 1);

                if (expireTs > 0 && GetServerTimestamp() > expireTs) {

                    if (signWxSignInGetQrCodeUrlFlagRef.current) {
                        return;
                    }

                    signWxSignInGetQrCodeUrlFlagRef.current = true

                    SignWxSignInGetQrCodeUrl({tenantId: tenantIdRef.current}, {

                        headers: {

                            hiddenErrorMsg: true

                        } as any

                    }).then(res => {

                        signWxSignInGetQrCodeUrlFlagRef.current = false

                        // 重置：二维码数据
                        sysSignConfigurationVORef.current.wxQrCodeSignUp = {}

                        if (res.data) {

                            // 再次设置：二维码数据
                            sysSignConfigurationVORef.current.wxQrCodeSignUp = res.data

                        }

                        // 更新页面
                        setSysSignConfigurationVO({...sysSignConfigurationVORef.current})

                    }).catch(() => {

                        signWxSignInGetQrCodeUrlFlagRef.current = false

                    })

                    return

                }

                if (signInTypeRef.current === SysSignTypeEnum.WxQrCode.code) {

                    if (sysSignConfigurationVORef.current.wxQrCodeSignUp.qrCodeId) {

                        SignWxSignInByQrCodeId({id: sysSignConfigurationVORef.current.wxQrCodeSignUp.qrCodeId}, {

                            headers: {

                                hiddenErrorMsg: true

                            } as any

                        }).then(res => {

                            if (res.data) {

                                clearInterval(interval)

                                // 完成登录
                                SignInSuccess(res.data)

                            }

                        })

                    }

                }

            }

        }, 1800);

        return () => {

            clearInterval(interval)

        }

    }, [])

    const [phoneSignInType, setPhoneSignInType] = useState<1 | 2>(1); // 手机号登录方式：1 验证码登录 2 密码登录

    return (

        <SignLayout tenantManageName={tenantManageName}>

            <LoginForm<ISignInForm>

                formRef={formRef}

                logo={IconSvg}

                title={tenantManageName}

                subTitle={CommonConstant.SYS_SUB_TITLE}

                actions={

                    (!sysSignInTypeEnum || sysSignInTypeEnum?.noSignUpLinkFlag) ? undefined :

                        <div>

                            或者

                            <Link title={"注册"}
                                  onClick={() => GetAppNav()(`${PathConstant.SIGN_UP_PATH}?tenantId=${tenantIdRef.current}`)}>注册</Link>

                        </div>

                }

                submitter={(!sysSignInTypeEnum || sysSignInTypeEnum?.noSignInBtnFlag) ? false : undefined}

                onFinish={async (form) => {

                    await SignInFormHandler({...form, tenantId: tenantIdRef.current, phoneSignInType, signInType})

                    return true

                }}

            >

                <Tabs activeKey={signInType} onChange={(activeKey) => {

                    formRef.current?.resetFields() // 重置表单
                    setSignInType(activeKey)

                }}

                      items={tabItemArr}>

                </Tabs>

                {

                    signInType === SysSignTypeEnum.SignInName.code && (

                        <>

                            <ProFormText

                                name="account"
                                fieldProps={{
                                    size: 'large',
                                    allowClear: true,
                                    prefix: SysSignTypeEnum.SignInName.prefix,
                                }}
                                placeholder={accountPlaceholder}
                                rules={[
                                    {
                                        required: true,
                                        message: '请输入' + accountPlaceholder
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

                    )

                }

                {

                    signInType === SysSignTypeEnum.Phone.code && (

                        <>

                            <ProFormText

                                name="phone"
                                fieldProps={{
                                    size: 'large',
                                    allowClear: true,
                                    prefix: sysSignInTypeEnum?.prefix,
                                }}

                                required
                                placeholder={sysSignInTypeEnum?.placeholder}
                                rules={[{validator: sysSignInTypeEnum?.validator}]}

                            />

                            {

                                phoneSignInType === 1 && <ProFormCaptcha

                                    fieldProps={{
                                        size: 'large',
                                        maxLength: 6,
                                        allowClear: true,
                                        prefix: <SafetyCertificateOutlined/>,
                                    }}

                                    captchaProps={{
                                        size: 'large',
                                    }}

                                    required
                                    placeholder={'请输入验证码'}
                                    name="code"

                                    rules={[{validator: Validate.code.validator}]}

                                    onGetCaptcha={async () => {

                                        await formRef.current?.validateFields(['phone']).then(async res => {

                                            await SignPhoneSignInSendCode({

                                                phone: res.phone,
                                                tenantId: GetTenantId()

                                            }).then(res => {

                                                ToastSuccess(res.msg)

                                            })

                                        })

                                    }}

                                />

                            }

                            {

                                phoneSignInType === 2 && <ProFormText.Password

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

                            }

                        </>

                    )

                }

                {signInType === SysSignTypeEnum.WxQrCode.code && sysSignConfigurationVO.wxQrCodeSignUp?.qrCodeUrl && (

                    <Image src={sysSignConfigurationVO.wxQrCodeSignUp.qrCodeUrl}
                           height={CommonConstant.QR_CODE_WIDTH} preview={false}/>

                )}

                {

                    (!sysSignInTypeEnum || sysSignInTypeEnum?.noForgetPasswordFlag) ? undefined :

                        <div
                            className={"m-b-24 flex"}
                        >

                            {

                                signInType === SysSignTypeEnum.Phone.code && <a className={"m-r-12"} onClick={() => {

                                    if (phoneSignInType === 1) {

                                        setPhoneSignInType(2)

                                    } else {

                                        setPhoneSignInType(1)

                                    }

                                }}>{phoneSignInType === 1 ? '密码登录' : '验证码登录'}</a>

                            }

                            {/* 忘记密码了 */}
                            <UserForgetPasswordModalForm sysSignInTypeEnum={sysSignInTypeEnum}/>

                        </div>

                }

            </LoginForm>

        </SignLayout>

    )

}

interface IUserForgetPasswordModalForm {

    sysSignInTypeEnum?: ISysSignTypeItemEnum

}

interface IUserForgetPasswordModalFormRef extends SignEmailForgetPasswordDTO, SignPhoneForgetPasswordDTO {

    account: string

}

const userForgetPasswordModalTitle = "忘记密码了"

export function UserForgetPasswordModalForm(props: IUserForgetPasswordModalForm) {

    const formRef = useRef<ProFormInstance<IUserForgetPasswordModalFormRef>>();

    const sysSignInTypeEnum = useMemo(() => {

        let res = props.sysSignInTypeEnum || SysSignTypeEnum.SignInName;

        if (res.code === SysSignTypeEnum.SignInName.code) {

            res = SysSignTypeEnum.Email

        }

        return res

    }, [props.sysSignInTypeEnum]);

    return <ModalForm<IUserForgetPasswordModalFormRef>

        modalProps={{
            maskClosable: false
        }}

        formRef={formRef}

        width={CommonConstant.MODAL_FORM_WIDTH}

        title={userForgetPasswordModalTitle}

        trigger={<a>{userForgetPasswordModalTitle}</a>}

        onFinish={async (form) => {

            const formTemp: IUserForgetPasswordModalFormRef = {...form, tenantId: GetTenantId()}

            formTemp.originNewPassword = RSAEncryptPro(formTemp.newPassword!)
            formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword!)

            if (sysSignInTypeEnum.code === SysSignTypeEnum.Phone.code) {

                await SignPhoneForgetPassword({...formTemp, phone: formTemp.account}).then(res => {
                    ToastSuccess(res.msg)
                })

            } else {

                await SignEmailForgetPassword({...formTemp, email: formTemp.account}).then(res => {
                    ToastSuccess(res.msg)
                })

            }

            return true

        }}

    >

        <ProFormText

            name="account"

            fieldProps={{
                allowClear: true,
            }}

            required

            label={sysSignInTypeEnum.placeholder}

            placeholder={'请输入' + sysSignInTypeEnum.placeholder}

            rules={[{validator: sysSignInTypeEnum.validator}]}

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

            rules={[{validator: Validate.code.validator}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['account']).then(async res => {

                    if (sysSignInTypeEnum.code === SysSignTypeEnum.Phone.code) {

                        await SignPhoneForgetPasswordSendCode({
                            phone: res.account,
                            tenantId: GetTenantId()
                        }).then(res => {

                            ToastSuccess(res.msg)

                        })

                    } else {

                        await SignEmailForgetPasswordSendCode({
                            email: res.account,
                            tenantId: GetTenantId()
                        }).then(res => {

                            ToastSuccess(res.msg)

                        })

                    }

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

            rules={[{validator: Validate.password.validator}]}

        />

    </ModalForm>

}
