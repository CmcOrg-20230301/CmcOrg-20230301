import {Image, Modal} from "antd";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import React, {useEffect, useRef, useState} from "react";
import {UserSelfSetWxModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import {MyUseState} from "@/util/HookUtil.ts";
import {GetQrCodeVO, SignSignInNameSetWx, SignSignInNameSetWxGetQrCodeUrl} from "@/api/http/SignSignInName.ts";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {ToastError, ToastSuccess} from "@/util/ToastUtil.ts";

export default function () {

    const [qrCodeModalOpen, setQrCodeModalOpen] = useState<boolean>(false);

    const [qrCodeVO, setQrCodeVO, qrCodeVORef] = MyUseState(useState<GetQrCodeVO | undefined>());

    useEffect(() => {

        SignSignInNameSetWxGetQrCodeUrl().then(res => {

            setQrCodeVO(res.data)

        })

    }, [])

    const getQrCodeUrlFlagRef = useRef<boolean>(false);

    useEffect(() => {

        const interval = setInterval(() => {

            if (qrCodeVORef.current) { // 如果：允许绑定微信

                // 二维码过期时间
                const expireTs = Number(qrCodeVORef.current.expireTs || 1);

                if (expireTs > 0 && GetServerTimestamp() > expireTs && !getQrCodeUrlFlagRef.current) {

                    getQrCodeUrlFlagRef.current = true

                    SignSignInNameSetWxGetQrCodeUrl().then(res => {

                        getQrCodeUrlFlagRef.current = false

                        // 更新：二维码数据
                        setQrCodeVO(res.data)

                    }).catch(() => {

                        getQrCodeUrlFlagRef.current = false

                    })

                    return

                }

                if (qrCodeVORef.current.qrCodeId) {

                    SignSignInNameSetWx({id: qrCodeVORef.current.qrCodeId}).then(res => {

                        if (res.data.sceneFlag) {

                            if (res.data.errorMsg) {

                                ToastError(res.data.errorMsg)

                            } else {

                                ToastSuccess(res.msg)

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

    return <Modal

        title={UserSelfSetWxModalTitle}
        open={qrCodeModalOpen}

        maskClosable={false}

        footer={null}

        onCancel={() => {

            setQrCodeModalOpen(false)

        }}

        className={"noFooterModal flex-center"}

    >

        {

            qrCodeVO?.qrCodeUrl &&

            <Image src={qrCodeVO.qrCodeUrl}
                   height={CommonConstant.QR_CODE_WIDTH} preview={false}/>

        }

    </Modal>

}