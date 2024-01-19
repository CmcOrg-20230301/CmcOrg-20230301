import React, {forwardRef, useEffect, useImperativeHandle, useRef, useState} from "react";
import {SysPayPayTradeStatusById} from "@/api/http/SysPay";
import {SysPayTradeStatusEnum} from "@/model/enum/SysPayTradeStatusEnum";
import {ToastSuccess} from "@/util/ToastUtil";
import {Modal, QRCode} from "antd";
import CommonConstant from "@/model/constant/CommonConstant";
import {GetSysPayTypeNamePre, SysPayTypeEnum, SysPayTypeEnumMap} from "@/model/enum/SysPayTypeEnum";
import {GetBrowserCategory} from "@/util/BrowserCategoryUtil";
import {useAppSelector} from "@/store";
import {SYS_PAY_CLOSE_MODAL} from "@/api/socket/WebSocket.ts";
import {SysRequestCategoryEnum} from "@/model/enum/SysRequestCategoryEnum";

/**
 * 获取：支付类型
 */
export function GetSysPayType(): undefined | number {

    if (window.WeixinJSBridge) {

        const browserCategory = GetBrowserCategory();

        if (browserCategory === SysRequestCategoryEnum.ANDROID_BROWSER_WX.code || browserCategory === SysRequestCategoryEnum.IOS_BROWSER_WX.code) {

            return SysPayTypeEnum.WX_JSAPI.code // 微信-jsApi

        } else {

            return undefined // 默认支付

        }

    } else {

        return undefined // 默认支付

    }

}

/**
 * 处理：是否购买成功
 */
export function UseEffectSysPayPayTradeStatusById(outTradeNoRef: React.MutableRefObject<string>, setQrCodeModalOpen: (value: (((prevState: boolean) => boolean) | boolean)) => void, props: IPayComponent) {

    useEffect(() => {

        const interval = setInterval(() => {

            if (outTradeNoRef.current) {

                SysPayPayTradeStatusById({id: outTradeNoRef.current}).then(res => {

                    if (res.data === SysPayTradeStatusEnum.TRADE_SUCCESS.code as any) {

                        // 处理：关闭弹窗
                        handleCloseModal(outTradeNoRef, setQrCodeModalOpen, props);

                    }

                })

            }

        }, 3000);

        return () => {

            clearInterval(interval)

        }

    }, [])

}

export interface IPayComponent {

    callBack?: () => void
    hiddenMsg?: boolean

}

export interface BuyVO {

    sysPayType?: number // 实际的支付方式，format：int32
    sysPayConfigurationId?: string // 支付配置主键 id，format：int64
    outTradeNo?: string // 本系统的支付订单号
    payReturnValue?: string // 支付返回的参数

}

export interface IPayComponentRef {

    HandleBuyVO: (buyVO: BuyVO) => void

}

// 处理：关闭弹窗
function handleCloseModal(outTradeNoRef: React.MutableRefObject<string>, setQrCodeModalOpen: (value: (((prevState: boolean) => boolean) | boolean)) => void, props: IPayComponent) {

    if (!outTradeNoRef.current) {
        return
    }

    outTradeNoRef.current = ""

    ToastSuccess("购买成功")

    setQrCodeModalOpen(false)

    if (props.callBack) {

        props.callBack()

    }

}

// 支付组件
const PayComponent = forwardRef<IPayComponentRef, IPayComponent>((props, ref) => {

    const [qrCodeValue, setQrCodeValue] = useState<string>("");

    const [qrCodeModalOpen, setQrCodeModalOpen] = useState<boolean>(false);

    const [qrCodeModalTitle, setQrCodeModalTitle] = useState<string | undefined>('请扫码支付');

    const outTradeNoRef = useRef<string>("") // 本系统支付主键 id，备注：如果存在，则需要去定时查询，是否支付成功

    // 设置：二维码的值，并且打开弹窗
    function SetQrCodeData(qrCodeValue: string, modalTitle: string, outTradeNo: string) {

        setQrCodeValue(qrCodeValue)

        setQrCodeModalTitle(modalTitle)

        setQrCodeModalOpen(true)

        outTradeNoRef.current = outTradeNo

    }

    // 处理：购买返回值
    function HandleBuyVO(buyVO: BuyVO) {

        const sysPayTypeEnum = SysPayTypeEnumMap.get(buyVO.sysPayType as any);

        // 扫码付款
        function ScanTheCodeToPay() {

            let text = sysPayTypeEnum?.name as any;

            if (text) {
                text = "请打开" + GetSysPayTypeNamePre(text as any) + "，进行扫码付款"
            }

            SetQrCodeData(buyVO.payReturnValue!, text || "", buyVO.outTradeNo!)

        }

        if (sysPayTypeEnum?.openPay) {

            // 打开支付
            sysPayTypeEnum.openPay(buyVO, ScanTheCodeToPay, props.callBack, props.hiddenMsg)

        } else {

            ScanTheCodeToPay();

        }

    }

    useImperativeHandle(ref, () => {

        return {
            HandleBuyVO
        }

    }, [])

    // 处理：是否购买成功
    UseEffectSysPayPayTradeStatusById(outTradeNoRef, setQrCodeModalOpen, props);

    const webSocketMessage = useAppSelector((state) => state.common.webSocketMessage);

    useEffect(() => {

        if (webSocketMessage.uri === SYS_PAY_CLOSE_MODAL) {

            if (outTradeNoRef.current && webSocketMessage.data === outTradeNoRef.current) {

                // 处理：关闭弹窗
                handleCloseModal(outTradeNoRef, setQrCodeModalOpen, props);

            }

        }

    }, [webSocketMessage])

    return <>

        <Modal

            title={qrCodeModalTitle}
            open={qrCodeModalOpen}

            maskClosable={false}

            footer={null}

            onCancel={() => {

                outTradeNoRef.current = ""

                setQrCodeModalOpen(false)

            }}

            className={"noFooterModal flex-center"}

        >

            <QRCode value={qrCodeValue} size={CommonConstant.QR_CODE_WIDTH} bordered={false}/>

        </Modal>

    </>

})

export default PayComponent
