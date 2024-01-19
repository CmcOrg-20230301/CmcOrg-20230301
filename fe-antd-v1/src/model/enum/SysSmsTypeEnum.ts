import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysSmsTypeEnum {

    ALI_YUN: IEnum<number>,

    TENCENT_YUN: IEnum<number>,

}

// 短信类型，枚举类
export const SysSmsTypeEnum: ISysSmsTypeEnum = {

    ALI_YUN: {
        code: 101,
        name: '阿里云',
    },

    TENCENT_YUN: {
        code: 201,
        name: '腾讯云',
    },

}

export const SysSmsTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysSmsTypeEnum).forEach(key => {

    const item = SysSmsTypeEnum[key] as IEnum<number>;

    SysSmsTypeEnumDict.set(item.code!, {text: item.name})

})
