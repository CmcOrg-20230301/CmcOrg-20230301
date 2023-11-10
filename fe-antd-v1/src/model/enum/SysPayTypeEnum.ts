import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";
import {ToastInfo, ToastSuccess} from "@/util/ToastUtil";
import {GetBrowserCategory} from "@/util/BrowserCategoryUtil";
import {BrowserCategoryEnum} from "@/model/enum/BrowserCategoryEnum";

export interface BuyVO {
    sysPayTypeEnum?: string // 实际的支付方式
    sysPayConfigurationId?: string // 支付配置主键 id，format：int64
    outTradeNo?: string // 本系统的支付订单号
    payReturnValue?: string // 支付返回的参数
}

export interface ISysUserWalletWithdrawTypeEnumItem extends IEnum {

    openPay?: (buyVO: BuyVO, ScanTheCodeToPay: () => void, callBack?: () => void, hiddenMsg?: boolean) => void | true // 打开支付，备注：默认是扫码付款

}

export interface ISysPayTypeEnum {

    ALI_QR_CODE: ISysUserWalletWithdrawTypeEnumItem,
    ALI_APP: ISysUserWalletWithdrawTypeEnumItem,
    ALI_WEB_PC: ISysUserWalletWithdrawTypeEnumItem,
    ALI_WEB_APP: ISysUserWalletWithdrawTypeEnumItem,

    WX_NATIVE: ISysUserWalletWithdrawTypeEnumItem,
    WX_JSAPI: ISysUserWalletWithdrawTypeEnumItem,

    UNION: ISysUserWalletWithdrawTypeEnumItem,
    GOOGLE: ISysUserWalletWithdrawTypeEnumItem,

}

// 支付方式类型枚举类
export const SysPayTypeEnum: ISysPayTypeEnum = {

    ALI_QR_CODE: {
        code: 101,
        name: '支付宝-扫码付款',
    },

    ALI_APP: {
        code: 102,
        name: '支付宝-手机支付',
    },

    ALI_WEB_PC: {
        code: 103,
        name: '支付宝-电脑网站支付',
    },

    ALI_WEB_APP: {
        code: 104,
        name: '支付宝-手机网站支付',
    },

    WX_NATIVE: {

        code: 201,
        name: '微信-native',

        openPay: (buyVO, ScanTheCodeToPay, callBack, hiddenMsg) => {

            if (window.WeixinJSBridge) {

                const browserCategory = GetBrowserCategory();

                if (browserCategory === BrowserCategoryEnum.ANDROID_BROWSER_WX.code || browserCategory === BrowserCategoryEnum.APPLE_BROWSER_WX.code) {

                    ToastInfo(buyVO.payReturnValue!, 10)

                } else {

                    ScanTheCodeToPay() // 扫码付款

                }

            } else {

                ScanTheCodeToPay() // 扫码付款

            }

        }

    },

    WX_JSAPI: {

        code: 202,
        name: '微信-jsApi',

        openPay: (buyVO, ScanTheCodeToPay, callBack, hiddenMsg) => {

            window.WeixinJSBridge.invoke('getBrandWCPayRequest', JSON.parse(buyVO.payReturnValue!),

                function (res: { err_msg: string }) {

                    if (res.err_msg === "get_brand_wcpay_request:ok") {

                        if (!hiddenMsg) {

                            ToastSuccess('购买成功')

                        }

                        if (callBack) {

                            callBack()

                        }

                    }

                }
            );

        }

    },

    UNION: {
        code: 301,
        name: '云闪付',
    },

    GOOGLE: {
        code: 401,
        name: '谷歌',
    },

}

export const SysPayTypeEnumMap = new Map<number, ISysUserWalletWithdrawTypeEnumItem>();

Object.keys(SysPayTypeEnum).forEach(key => {

    const item = SysPayTypeEnum[key];

    SysPayTypeEnumMap.set(item.code as number, item)

})

export const SysPayTypeDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysPayTypeEnum).forEach(key => {

    const item = SysPayTypeEnum[key];

    SysPayTypeDict.set(item.code as number, {text: item.name})

})

/**
 * 获取：支付名称的前缀
 */
export function GetSysPayTypeNamePre(name?: string) {

    if (!name) {
        return ""
    }

    return name!.split("-")[0]

}
