import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysUserWalletWithdrawTypeEnum {

    ALI_QR_CODE: IEnum,
    ALI_APP: IEnum,
    ALI_WEB_PC: IEnum,
    ALI_WEB_APP: IEnum,

    WX_NATIVE: IEnum,
    WX_JSAPI: IEnum,

    UNION: IEnum,
    GOOGLE: IEnum,

}

// 支付方式类型枚举类
export const SysPayTypeEnum: ISysUserWalletWithdrawTypeEnum = {

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
    },

    WX_JSAPI: {
        code: 202,
        name: '微信-jsApi',
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
