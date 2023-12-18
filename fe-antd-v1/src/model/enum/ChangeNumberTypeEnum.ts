import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface IChangeNumberTypeEnum {

    ADD: IEnum<number>,

    DEDUCT: IEnum<number>,

}

// 操作数字时的类型，枚举类
export const ChangeNumberTypeEnum: IChangeNumberTypeEnum = {

    ADD: {
        code: 101,
        name: '增加',
    },

    DEDUCT: {
        code: 201,
        name: '扣除',
    },

}

export const ChangeNumberTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(ChangeNumberTypeEnum).forEach(key => {

    const item = ChangeNumberTypeEnum[key];

    ChangeNumberTypeEnumDict.set(item.code as number, {text: item.name})

})
