import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysOtherAppTypeEnum {

    WX_MINI_PROGRAM: IEnum<number>,

    WX_OFFICIAL_ACCOUNT: IEnum<number>,

    ALI_PAY_PROGRAM: IEnum<number>,

    BAI_DU: IEnum<number>,

}

// 第三方应用类型，枚举类
export const SysOtherAppTypeEnum: ISysOtherAppTypeEnum = {

    WX_MINI_PROGRAM: {
        code: 101,
        name: '微信小程序',
    },

    WX_OFFICIAL_ACCOUNT: {
        code: 102,
        name: '微信公众号',
    },

    ALI_PAY_PROGRAM: {
        code: 201,
        name: '支付宝小程序',
    },

    BAI_DU: {
        code: 301,
        name: '百度',
    },

}

export const SysOtherAppTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysOtherAppTypeEnum).forEach(key => {

    const item = SysOtherAppTypeEnum[key];

    SysOtherAppTypeEnumDict.set(item.code as number, {text: item.name})

})
