import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysOtherAppOfficialAccountMenuButtonTypeEnum {

    CLICK: IEnum<number>,

    VIEW: IEnum<number>,

}

// 第三方应用，公众号按钮类型，枚举类
export const SysOtherAppOfficialAccountMenuButtonTypeEnum: ISysOtherAppOfficialAccountMenuButtonTypeEnum = {

    CLICK: {
        code: 101,
        name: '按钮',
    },

    VIEW: {
        code: 201,
        name: '链接',
    },

}

export const SysOtherAppOfficialAccountMenuButtonTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysOtherAppOfficialAccountMenuButtonTypeEnum).forEach(key => {

    const item = SysOtherAppOfficialAccountMenuButtonTypeEnum[key] as IEnum<number>;

    SysOtherAppOfficialAccountMenuButtonTypeEnumDict.set(item.code!, {text: item.name})

})
