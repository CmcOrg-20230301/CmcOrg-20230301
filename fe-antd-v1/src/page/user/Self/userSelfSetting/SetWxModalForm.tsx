import {Image, Result} from "antd";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import React, {useEffect, useRef, useState} from "react";
import {UserSelfSetWxModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {MyUseState} from "@/util/HookUtil.ts";
import {GetQrCodeVO} from "@/api/http/SignSignInName.ts";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {ToastError, ToastSuccess} from "@/util/ToastUtil.ts";
import {AxiosRequestConfig} from "axios";
import {ApiResultVO} from "@/util/HttpUtil.ts";
import {NotNullId, SysQrCodeSceneBindVO} from "@/api/http/SignWx.ts";
import {ModalForm, ProFormGroup, ProFormInstance} from "@ant-design/pro-components";

export interface ISetWxModalForm {

    // 获取：二维码
    setWxGetQrCodeUrl(config?: AxiosRequestConfig<any> | undefined): Promise<ApiResultVO<GetQrCodeVO>>

    // 获取：二维码是否扫描
    setWxGetQrCodeSceneFlag(form: NotNullId, config?: AxiosRequestConfig<any> | undefined): Promise<ApiResultVO<SysQrCodeSceneBindVO>>

    // 绑定微信
    setWx(form: any, config?: AxiosRequestConfig<any> | undefined): Promise<ApiResultVO<SysQrCodeSceneBindVO>>

    // 表单数组
    formItemArr: JSX.Element[]

    // 处理：表单数据
    handleFormFun?: (form: any) => void;

    // 标题
    title?: string

}

export default function (props: ISetWxModalForm) {

    const qrCodeModalOpenRef = useRef<boolean>(false);

    // 二维码是否已经扫码
    const [qrCodeSceneFlag, setQrCodeSceneFlag, qrCodeSceneFlagRef] = MyUseState(useState<boolean>(false));

    const [qrCodeVO, setQrCodeVO, qrCodeVORef] = MyUseState(useState<GetQrCodeVO | undefined>(), newState => {

        // 重置：二维码扫描状态
        setQrCodeSceneFlag(false)

    });

    const formRef = useRef<ProFormInstance>();

    useEffect(() => {

        props.setWxGetQrCodeUrl().then(res => {

            setQrCodeVO(res.data)

        })

    }, [])

    // 是否：在获取二维码
    const getQrCodeUrlFlagRef = useRef<boolean>(false);

    useEffect(() => {

        const interval = setInterval(() => {

            if (qrCodeVORef.current) { // 如果：允许绑定微信

                // 二维码过期时间
                const expireTs = Number(qrCodeVORef.current.expireTs || 1);

                if (expireTs > 0 && GetServerTimestamp() > expireTs && !getQrCodeUrlFlagRef.current) {

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

                if (qrCodeModalOpenRef.current && qrCodeVORef.current.qrCodeId && !qrCodeSceneFlagRef.current) {

                    props.setWxGetQrCodeSceneFlag({id: qrCodeVORef.current.qrCodeId}).then(res => {

                        if (res.data.sceneFlag) {

                            // 设置：已经扫描二维码
                            setQrCodeSceneFlag(true)

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
                destroyOnClose: true,
            }}

            isKeyPressSubmit
            width={CommonConstant.MODAL_FORM_WIDTH}

            title={props.title || UserSelfSetWxModalTitle}
            trigger={<a>{props.title || UserSelfSetWxModalTitle}</a>}

            onOpenChange={(visible: boolean) => {

                qrCodeModalOpenRef.current = visible

            }}

            onFinish={async (form) => {

                if (!qrCodeSceneFlag) {

                    ToastError("操作失败：请先微信扫码")
                    return

                }

                if (props.handleFormFun) { // 处理：表单数据

                    props.handleFormFun(form)

                }

                await props.setWx({...form, qrCodeId: qrCodeVO?.qrCodeId, id: qrCodeVO?.qrCodeId}).then(res => {

                    if (res.data.sceneFlag) {

                        if (res.data.errorMsg) {

                            ToastError(res.data.errorMsg)

                        } else {

                            ToastSuccess(res.msg)

                        }

                    }

                })

                return true

            }}

        >

            {

                props.formItemArr.map(item => item)

            }

            <ProFormGroup title="微信扫码">

                {

                    qrCodeSceneFlag &&

                    <Result
                        status="success"
                        title="扫码成功"
                    />

                }

                {

                    (qrCodeVO?.qrCodeUrl && !qrCodeSceneFlag) &&

                    <Image src={qrCodeVO.qrCodeUrl}
                           height={CommonConstant.QR_CODE_WIDTH} preview={false}/>

                }

            </ProFormGroup>

        </ModalForm>

    </>

}