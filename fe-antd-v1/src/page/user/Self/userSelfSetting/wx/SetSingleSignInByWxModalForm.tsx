import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {
    UserSelfSetSingleSignInWxModalTitle,
    UserSelfUpdateSingleSignInWxModalTitle
} from "@/page/user/Self/UserSelfSetting.tsx";
import React, {useEffect, useRef, useState} from "react";
import {Form, Image, Result} from "antd";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {MyUseState} from "@/util/HookUtil.ts";
import {GetQrCodeVO} from "@/api/http/SignSignInName.ts";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {ToastError} from "@/util/ToastUtil.ts";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import {
    SignWxSetSingleSignInWx,
    SignWxSetSingleSignInWxGetQrCodeSceneFlag,
    SignWxSetSingleSignInWxGetQrCodeSceneFlagCurrent,
    SignWxSetSingleSignInWxGetQrCodeUrl,
    SignWxSetSingleSignInWxGetQrCodeUrlCurrent
} from "@/api/http/SignWx.ts";

interface ISetSingleSignInByWxModalForm {

    userSelfInfo: UserSelfInfoVO

}

export default function (props: ISetSingleSignInByWxModalForm) {

    const qrCodeModalOpenFlagRef = useRef<boolean>(false);

    // 二维码是否已经扫码
    const [qrCodeSceneFlag, setQrCodeSceneFlag, qrCodeSceneFlagRef] = MyUseState(useState<boolean>(false));

    const [qrCodeVO, setQrCodeVO, qrCodeVORef] = MyUseState(useState<GetQrCodeVO | undefined>(), newState => {

        // 重置：二维码扫描状态
        setQrCodeSceneFlag(false)

    });

    // 初始化数据
    function InitData() {

        SignWxSetSingleSignInWxGetQrCodeUrl().then(res => {

            setQrCodeVO(res.data)

        })

    }

    // 是否：在获取二维码
    const getQrCodeUrlFlagRef = useRef<boolean>(false);

    useEffect(() => {

        const interval = setInterval(() => {

            if (qrCodeVORef.current) { // 如果：允许绑定微信

                // 二维码过期时间
                const expireTs = Number(qrCodeVORef.current.expireTs || 1);

                if (expireTs > 0 && GetServerTimestamp() > expireTs) {

                    if (getQrCodeUrlFlagRef.current) {
                        return;
                    }

                    getQrCodeUrlFlagRef.current = true

                    SignWxSetSingleSignInWxGetQrCodeUrl().then(res => {

                        getQrCodeUrlFlagRef.current = false

                        // 更新：二维码数据
                        setQrCodeVO(res.data)

                    }).catch(() => {

                        getQrCodeUrlFlagRef.current = false

                    })

                    return

                }

                if (qrCodeModalOpenFlagRef.current && qrCodeVORef.current.qrCodeId && !qrCodeSceneFlagRef.current) {

                    SignWxSetSingleSignInWxGetQrCodeSceneFlag({id: qrCodeVORef.current.qrCodeId}).then(res => {

                        if (res.data.sceneFlag) {

                            if (res.data.errorMsg) {

                                ToastError(res.data.errorMsg)
                                InitData() // 重新：初始化数据

                            } else {

                                // 设置：已经扫描二维码
                                setQrCodeSceneFlag(true)

                            }

                        }

                    })

                }

            }

        }, 1800);

        return () => {

            clearInterval(interval)

        }

    }, [])

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxSetSingleSignInWxGetQrCodeUrlCurrent}
                        setWxGetQrCodeSceneFlag={SignWxSetSingleSignInWxGetQrCodeSceneFlagCurrent}
                        setWx={SignWxSetSingleSignInWx}

                        title={props.userSelfInfo.singleSignInWxFlag ? UserSelfUpdateSingleSignInWxModalTitle : UserSelfSetSingleSignInWxModalTitle}

                        label={"当前账号微信扫码"}

                        handleFormFun={(form, qrCodeId) => {

                            form.currentQrCodeId = qrCodeId
                            form.singleSignInQrCodeId = qrCodeVORef.current?.qrCodeId

                        }}

                        reQrCodeScene={() => {

                            InitData()

                        }}

                        onOpenChange={(visible) => {

                            if (visible && qrCodeVORef.current === undefined) {

                                InitData() // 初始化数据

                            }

                            if (!visible && qrCodeSceneFlagRef.current) {

                                InitData() // 关闭时，如果已经扫码了，则重新：初始化数据

                            }

                            qrCodeModalOpenFlagRef.current = visible

                        }}

                        formItemArr={formRef => [

                            <Form.Item key={"1"} label="统一登录微信扫码" required={true}>

                                {

                                    qrCodeSceneFlag &&

                                    <div style={{height: CommonConstant.QR_CODE_WIDTH + 'px'}}
                                         className={"flex-center"}>

                                        <Result
                                            status="success"
                                            title="扫码成功"
                                        />

                                    </div>


                                }

                                {

                                    (qrCodeVO?.qrCodeUrl && !qrCodeSceneFlag) &&

                                    <Image src={qrCodeVO.qrCodeUrl}
                                           height={CommonConstant.QR_CODE_WIDTH} preview={false}/>

                                }

                            </Form.Item>

                        ]}

        />

    </>

}