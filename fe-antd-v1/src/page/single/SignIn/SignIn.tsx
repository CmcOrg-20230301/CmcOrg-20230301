import {useEffect, useMemo, useRef, useState} from "react";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {ISysSignTypeItemEnum, SysSignTypeEnum, SysSignTypeEnumMap} from "@/model/enum/SysSignTypeEnum.tsx";
import {SignInFormHandler, SignInSuccess} from "@/page/sign/SignIn/SignInUtil.ts";
import SignLayout from "@/layout/SignLayout/SignLayout";
import {LoginForm, ProFormCaptcha, ProFormInstance, ProFormText} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil.ts";
import {ToastError, ToastSuccess} from "@/util/ToastUtil.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {MyUseState} from "@/util/HookUtil.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {SysSignConfigurationVO} from "@/api/http/SysTenant.ts";
import {ISignInForm, SetSysSignConfigurationVOCallBack} from "@/page/sign/SignIn/SignIn.tsx";
import PathConstant from "@/model/constant/PathConstant.ts";
import {Image, Tabs} from "antd";
import type {Tab} from "rc-tabs/lib/interface";
import {CloseWebSocket} from "@/util/WebSocket/WebSocketUtil.ts";
import IconSvg from '../../../../public/icon.svg'
import {LockOutlined, SafetyCertificateOutlined} from "@ant-design/icons";
import {
    SignSingleSignInByQrCodeIdWx,
    SignSingleSignInGetConfiguration,
    SignSingleSignInGetQrCodeUrlWx,
    SignSingleSignInSendCodePhone
} from "@/api/http/SignSingle.ts";
import {MyLocalStorage} from "@/util/StorageUtil.ts";

const tenantManageName = "灵秀AI Agent"

// 统一登录
export default function () {

    const formRef = useRef<ProFormInstance<ISignInForm>>();

    // 展示的登录方式
    const [tabItemArr, setTabItemArr] = useState<Tab[]>([]);

    const [signInType, setSignInType, signInTypeRef] = MyUseState(useState<string>(MyLocalStorage.getItem(LocalStorageKey.SIGN_IN_TYPE_SINGLE) || ""));

    const [accountPlaceholder, setAccountPlaceholder] = useState<string>("");

    const [sysSignConfigurationVO, setSysSignConfigurationVO, sysSignConfigurationVORef] =

        MyUseState(
            useState<SysSignConfigurationVO>({}),

            newState => {

                SetSysSignConfigurationVOCallBack(signInTypeRef.current, setSignInType, setTabItemArr, setAccountPlaceholder, 1, false)(newState)

                MyLocalStorage.setItem(LocalStorageKey.SYS_SIGN_CONFIGURATION_VO_SINGLE, JSON.stringify(newState))

            }
        )

    useEffect(() => {

        CloseWebSocket() // 关闭 webSocket

        // 为了触发：callBack
        setSysSignConfigurationVO(JSON.parse(MyLocalStorage.getItem(LocalStorageKey.SYS_SIGN_CONFIGURATION_VO_SINGLE) || "{}"))

        // 统一登录相关的配置
        SignSingleSignInGetConfiguration().then(res => {

            setSysSignConfigurationVO(res.data)

        })

    }, [])

    const sysSignInTypeEnum: ISysSignTypeItemEnum | undefined = useMemo(() => {

        const sysSignTypeItemEnum = SysSignTypeEnumMap.get(signInType) || SysSignTypeEnum.Phone;

        MyLocalStorage.setItem(LocalStorageKey.SIGN_IN_TYPE_SINGLE, sysSignTypeItemEnum.code!)

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

                    SignSingleSignInGetQrCodeUrlWx({

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

                        SignSingleSignInByQrCodeIdWx({id: sysSignConfigurationVORef.current.wxQrCodeSignUp.qrCodeId}, {

                            headers: {

                                hiddenErrorMsg: true

                            } as any

                        }).then(res => {

                            if (res.data) {

                                if (res.data.jwt) {

                                    clearInterval(interval)

                                    // 设置：为统一登录的页面
                                    MyLocalStorage.setItem(LocalStorageKey.NO_JWT_URI, PathConstant.SINGLE_SIGN_IN_PATH)

                                    // 完成登录
                                    SignInSuccess(res.data)

                                } else {

                                    sysSignConfigurationVORef.current.wxQrCodeSignUp = {} // 重新：获取二维码

                                    ToastError('操作失败：该微信未设置统一登录，请在【个人中心-统一登录】处，进行设置后再试')

                                }

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

    return <>

        <SignLayout tenantManageName={tenantManageName}>

            <LoginForm<ISignInForm>

                formRef={formRef}

                logo={IconSvg}

                title={tenantManageName}

                subTitle={CommonConstant.SYS_SUB_TITLE}

                submitter={(!sysSignInTypeEnum || sysSignInTypeEnum?.noSignInBtnFlag) ? false : undefined}

                onFinish={async (form) => {

                    await SignInFormHandler({...form, phoneSignInType, signInType, singleSignInFlag: true})

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

                                            await SignSingleSignInSendCodePhone({

                                                phone: res.phone,

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

            </LoginForm>

        </SignLayout>

    </>

}