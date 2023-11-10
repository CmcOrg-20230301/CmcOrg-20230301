import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysOtherAppOfficialAccountMenuButtonTypeEnum {

    CLICK: IEnum,

    VIEW: IEnum,

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

export const SysOtherAppOfficialAccountMenuButtonTypeDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysOtherAppOfficialAccountMenuButtonTypeEnum).forEach(key => {

    const item = SysOtherAppOfficialAccountMenuButtonTypeEnum[key];

    SysOtherAppOfficialAccountMenuButtonTypeDict.set(item.code as number, {text: item.name})

})
