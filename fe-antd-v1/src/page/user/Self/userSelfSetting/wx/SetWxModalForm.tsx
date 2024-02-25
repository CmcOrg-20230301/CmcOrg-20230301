import {Form, Image, Result} from "antd";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import React, {useEffect, useRef, useState} from "react";
import {UserSelfSetWxModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {MyUseState} from "@/util/HookUtil.ts";
import {GetQrCodeVO} from "@/api/http/SignSignInName.ts";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {ToastError, ToastSuccess} from "@/util/ToastUtil.ts";
import {ApiResultVO, IHttpConfig} from "@/util/HttpUtil.ts";
import {NotNullId, SysQrCodeSceneBindVO} from "@/api/http/SignWx.ts";
import {ModalForm, ProFormInstance} from "@ant-design/pro-components";
import {SignOut} from "@/util/UserUtil.ts";

export interface ISetWxModalForm {

    // 获取：二维码
    setWxGetQrCodeUrl: (config?: IHttpConfig<any> | undefined) => Promise<ApiResultVO<GetQrCodeVO>>

    // 获取：二维码是否扫描
    setWxGetQrCodeSceneFlag: (form: NotNullId, config?: IHttpConfig<any> | undefined) => Promise<ApiResultVO<SysQrCodeSceneBindVO>>

    // 绑定微信
    setWx: (form: any, config?: IHttpConfig<any> | undefined) => Promise<ApiResultVO<SysQrCodeSceneBindVO>>

    // 表单数组
    formItemArr?: (formRef: React.MutableRefObject<ProFormInstance | undefined>) => JSX.Element[]

    // 处理：表单数据
    handleFormFun?: (form: any, qrCodeId: string | undefined) => void;

    // 标题
    title?: string

    trigger?: JSX.Element;

    label?: string

    // 当弹窗关闭/打开时
    onOpenChange?: (visible: boolean) => void

    // 需要重新扫码时
    reQrCodeScene?: () => void

    // 退出登录时，需要跳转的页面
    signOutPath?: string

}

export default function (props: ISetWxModalForm) {

    const qrCodeModalOpenFlagRef = useRef<boolean>(false);

    // 二维码是否已经扫码
    const [qrCodeSceneFlag, setQrCodeSceneFlag, qrCodeSceneFlagRef] = MyUseState(useState<boolean>(false));

    const [qrCodeVO, setQrCodeVO, qrCodeVORef] = MyUseState(useState<GetQrCodeVO | undefined | null>(undefined), newState => {

        // 重置：二维码扫描状态
        setQrCodeSceneFlag(false)

    });

    const formRef = useRef<ProFormInstance>();

    // 初始化数据
    function InitData() {

        props.setWxGetQrCodeUrl().then(res => {

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

                    props.setWxGetQrCodeUrl().then(res => {

                        getQrCodeUrlFlagRef.current = false

                        // 更新：二维码数据
                        setQrCodeVO(res.data)

                    }).catch(() => {

                        getQrCodeUrlFlagRef.current = false

                    })

                    return

                }

                if (qrCodeModalOpenFlagRef.current && qrCodeVORef.current.qrCodeId && !qrCodeSceneFlagRef.current) {

                    props.setWxGetQrCodeSceneFlag({id: qrCodeVORef.current.qrCodeId}).then(res => {

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

        <ModalForm

            formRef={formRef}

            modalProps={{
                maskClosable: false,
            }}

            isKeyPressSubmit
            width={CommonConstant.MODAL_FORM_WIDTH}

            title={props.title || UserSelfSetWxModalTitle}
            trigger={props.trigger || <a>{props.title || UserSelfSetWxModalTitle}</a>}

            onOpenChange={(visible) => {

                if (visible && qrCodeVORef.current === undefined) {

                    InitData() // 初始化数据

                }

                if (!visible && qrCodeSceneFlagRef.current) {

                    InitData() // 关闭时，如果已经扫码了，则重新：初始化数据

                }

                if (props.onOpenChange) {

                    props.onOpenChange(visible)

                }

                qrCodeModalOpenFlagRef.current = visible

            }}

            onFinish={async (form) => {

                if (!qrCodeSceneFlagRef.current) {

                    ToastError("操作失败：请先微信扫码")
                    return

                }

                if (props.handleFormFun) { // 处理：表单数据

                    props.handleFormFun(form, qrCodeVORef.current?.qrCodeId)

                }

                let resBoolean = true

                // 重新：扫码二维码
                function ReQrCodeScene() {

                    setQrCodeSceneFlag(false) // 立即设置为：未扫码，目的：防止重复点击提交

                    InitData() // 重新：初始化数据

                    if (props.reQrCodeScene) {

                        props.reQrCodeScene() // 重新：初始化数据

                    }

                    resBoolean = false

                }

                await props.setWx({

                    ...form,

                    qrCodeId: qrCodeVORef.current?.qrCodeId,

                    id: qrCodeVORef.current?.qrCodeId

                }).then(res => {

                    if (res.data.sceneFlag) {

                        if (res.data.errorMsg) {

                            ReQrCodeScene(); // 重新：扫码二维码
                            ToastError(res.data.errorMsg)

                        } else {

                            SignOut(undefined, props.signOutPath)
                            ToastSuccess(res.msg)

                        }

                    } else {

                        ReQrCodeScene(); // 重新：扫码二维码
                        ToastError("操作超时：请重新扫描二维码")

                    }

                }).catch(() => {

                    ReQrCodeScene(); // 重新：扫码二维码

                })

                return resBoolean

            }}

        >

            {

                props.formItemArr && props.formItemArr(formRef).map(item => item)

            }

            <Form.Item label={props.label || "当前微信扫码"} required={true}>

                {

                    qrCodeSceneFlag &&

                    <div style={{height: CommonConstant.QR_CODE_WIDTH + 'px'}} className={"flex-center"}>

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

        </ModalForm>

    </>

}