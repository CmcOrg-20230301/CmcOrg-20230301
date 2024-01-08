import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysSocketOnlineTypeEnum {

    ONLINE: IEnum<number>,

    HIDDEN: IEnum<number>,

    PING_TEST: IEnum<number>,

}

// socket 在线状态，枚举类
export const SysSocketOnlineTypeEnum: ISysSocketOnlineTypeEnum = {

    ONLINE: {
        code: 101,
        name: '在线',
    },

    HIDDEN: {
        code: 201,
        name: '隐身',
    },

    PING_TEST: {
        code: 100001,
        name: 'ping测试',
    },

}

export const SysSocketOnlineTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysSocketOnlineTypeEnum).forEach(key => {

    const item = SysSocketOnlineTypeEnum[key];

    SysSocketOnlineTypeEnumDict.set(item.code as number, {text: item.name})

})
